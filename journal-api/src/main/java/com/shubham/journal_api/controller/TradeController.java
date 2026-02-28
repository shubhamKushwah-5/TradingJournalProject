package com.shubham.journal_api.controller;

import com.shubham.journal_api.exception.TradeNotFoundException;
import com.shubham.journal_api.model.Trade;
import com.shubham.journal_api.repository.TradeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades") //base url for all endpoints
public class TradeController{

    @Autowired //spring will inject repository dependency
    private TradeRepository tradeRepository;

    //temporary storage
   // private List<Trade> trades = new ArrayList<>();

    //Get all trades
    @GetMapping
    public List<Trade> getAllTrades(){
        return tradeRepository.findAll();
    }

    // GET single trade by ID
    @GetMapping("/{id}")
    public Trade getTradeById(@PathVariable Long id) {
        return tradeRepository.findById(id).orElseThrow(() -> new TradeNotFoundException(id));
    }

    //add new trade
    @PostMapping
    public ResponseEntity<Trade> addNewTrade(@Valid @RequestBody Trade trade){
        Trade savedTrade = tradeRepository.save(trade);
        return new ResponseEntity<>(savedTrade, HttpStatus.CREATED);  //201 status will be there


    }

    //counting trades number
//    @GetMapping("/count")
//    public String getTradeCount(){
//        return "Total trades: " + trade.size();
//
//    }
//
//    //trade pnl
//    @GetMapping("/{index}/pnl")
//    public String getTradePnL(@PathVariable int index){
//        if(index >= 0 && index < trade.size()) {
//            Trade trade = trade.get(index);
//            double pnl = trade.calculatePnL();
//            return String.format("P&L for %s: ₹%.2f %s",
//                    trade.getSymbol(), pnl, pnl > 0 ? "✓" : "✗");
//        }
//        return "Trade not found" ;
//    }

    //total pnl
    @GetMapping("/stats/total-pnl")
    public double getTotalPnL() {
        List<Trade> trades = tradeRepository.findAll();
        double total = 0;
        for (Trade trade : trades) {
            total += trade.calculatePnL();
        }
        return total;
    }

    //winrate
    @GetMapping("/stats/winrate")
    public String getWinRate(){
        List<Trade> trades = tradeRepository.findAll();

        if(trades.isEmpty()) return "No trades yet ";

        int wins =0;
        for(Trade trade : trades) {
            if(trade.calculatePnL() > 0) wins ++;
        }
        double winRate = (wins * 100.0) / trades.size();
        return String.format("Win rate: %.1f%%", winRate);
    }

    //put - update trade
    @PutMapping("/{id}")
    public Trade updateTrade(@PathVariable Long id, @Valid @RequestBody Trade tradeDetails){
        Trade trade = tradeRepository.findById(id).orElseThrow(() ->new TradeNotFoundException(id));

            trade.setSymbol(tradeDetails.getSymbol());
            trade.setType(tradeDetails.getType());
            trade.setEntryPrice(tradeDetails.getEntryPrice());
            trade.setExitPrice(tradeDetails.getExitPrice());
            trade.setQuantity(tradeDetails.getQuantity());
            trade.setStrategy(tradeDetails.getStrategy());

            return tradeRepository.save(trade);

    }

    //Delete trade
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrade(@PathVariable Long id){
        if (!tradeRepository.existsById(id)){
            throw new TradeNotFoundException(id);
        }
        tradeRepository.deleteById(id);
        return ResponseEntity.ok("Trade deleted with id: " + id);

    }

    //get trades by strategy
    @GetMapping("/strategy/{strategy}")
    public List<Trade> getTradesByStrategy(@PathVariable String strategy) {
        return tradeRepository.findByStrategy(strategy);
    }

    //GET best trade(highest pnl)
    @GetMapping("/stats/best-trade")
    public ResponseEntity<Trade> getBestTrade(){
        List<Trade> trades = tradeRepository.findAll();
        if(trades.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Trade best = trades.stream()
                .max((t1,t2) -> Double.compare(t1.calculatePnL(), t2.calculatePnL()))
                .orElse(null);

        return ResponseEntity.ok(best);
    }

    //GET worst trade(lowest pnl)
    @GetMapping("stats/worst-trade")
    public ResponseEntity<Trade> getWorstTrade(){
        List<Trade> trades = tradeRepository.findAll();
        if(trades.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Trade worst = trades.stream()
                .min((t1,t2) -> Double.compare(t1.calculatePnL(),t2.calculatePnL()))
                .orElse(null);

        return ResponseEntity.ok(worst);
    }

    //GET average pnl per trade
    @GetMapping("/stats/avg-pnl")
    public double getAveragePnl(){
        List<Trade> trades = tradeRepository.findAll();
        if(trades.isEmpty()) return 0;

        return trades.stream()
                .mapToDouble(Trade::calculatePnL)
                .average()
                .orElse(0);
    }


    //GET statistics by Strategy
    @GetMapping("/stats/by-strategy")
    public List<Map<String,Object>> getStatsByStrategy(){
        List<Trade> allTrades = tradeRepository.findAll();

        //group by strategy
        Map<String,List<Trade>> byStrategy = new HashMap<>();
        for(Trade trade : allTrades) {
            byStrategy.computeIfAbsent(trade.getStrategy(),k -> new ArrayList<>())
                    .add(trade);
        }

        //calculate stats for each strategy
        List<Map<String , Object>> result = new ArrayList<>();
        for(Map.Entry<String, List<Trade>> entry : byStrategy.entrySet()) {
            String strategy = entry.getKey();
            List<Trade> trades = entry.getValue();

            double totalPnl = trades.stream()
                    .mapToDouble(Trade::calculatePnL)
                    .sum();

            long wins = trades.stream()
                    .filter(t -> t.calculatePnL() > 0)
                    .count();

            double winRate = (wins * 100.0) / trades.size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("Strategy", strategy);
            stats.put("totalTrades", trades.size());
            stats.put("totalPnl", totalPnl);
            stats.put("winRate", winRate);
            stats.put("avgPnl", totalPnl / trades.size());

            result.add(stats);
        }

        return result;
    }

    //GET trades count by symbol
    @GetMapping("/stats/by-symbol")
    public List<Map<String,Object>> getTradesBySymbol() {
        List<Trade> allTrades = tradeRepository.findAll();

        Map<String, Long> countBySymbol = new HashMap<>();
        for(Trade trade : allTrades){
            countBySymbol.put(trade.getSymbol(),
                    countBySymbol.getOrDefault(trade.getSymbol(), 0L) +1);

        }

        List<Map<String, Object>> result = new ArrayList<>();
        for(Map.Entry<String, Long> entry : countBySymbol.entrySet()){
            Map<String,Object> symbolData = new HashMap<>();
            symbolData.put("symbol", entry.getKey());
            symbolData.put("count", entry.getValue());
            result.add(symbolData);
        }

        return result;
    }


    //GET win/loss breakdown
    @GetMapping("/stats/win-loss")
    public Map<String,Object> getWinLossBreakdown(){
        List<Trade> trades = tradeRepository.findAll();

        long wins = trades.stream()
                .filter(t -> t.calculatePnL() > 0)
                .count();

        long losses = trades.size()-wins;

        double totalWinPnl = trades.stream()
                .filter(t -> t.calculatePnL() > 0)
                .mapToDouble(Trade::calculatePnL)
                .sum();

        double totalLossPnL = trades.stream()
                .filter(t -> t.calculatePnL() <= 0)
                .mapToDouble(Trade::calculatePnL)
                .sum();

        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("totalTrades", trades.size());
        breakdown.put("wins", wins);
        breakdown.put("losses", losses);
        breakdown.put("winRate", wins * 100.0 / trades.size());
        breakdown.put("totalWinPnl", totalWinPnl);
        breakdown.put("totalLossPnl", totalLossPnL);
        breakdown.put("avgWin", wins > 0 ? totalWinPnl/wins:0);
        breakdown.put("avgLoss", losses > 0 ? totalLossPnL/losses: 0);

        return breakdown;
    }




}
