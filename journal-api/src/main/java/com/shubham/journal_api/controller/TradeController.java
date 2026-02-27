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
import java.util.List;

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


}
