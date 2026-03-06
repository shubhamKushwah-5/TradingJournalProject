package com.shubham.journal_api.controller;

import com.shubham.journal_api.model.Trade;
import com.shubham.journal_api.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
public class TradeController{

    @Autowired
    private TradeService tradeService;

    //                 BASIC CRUD CONTROLLERS

    // GET all trades for logged-in user
    @GetMapping
    public List<Trade> getAllTrades(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getUserTrades(username);
    }

    // GET single trade by ID
    @GetMapping("/{id}")
    public Trade getTradeById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getTradeById(id, username);
    }

    // POST - add new trade
    @PostMapping
    public ResponseEntity<Trade> addNewTrade(@Valid @RequestBody Trade trade, Authentication authentication){
        String username = authentication.getName();
        Trade savedTrade = tradeService.addTrade(trade, username);
        return new ResponseEntity<>(savedTrade, HttpStatus.CREATED);
    }

    // PUT - update trade
    @PutMapping("/{id}")
    public Trade updateTrade(@PathVariable Long id,
                             @Valid @RequestBody Trade tradeDetails,
                             Authentication authentication){
        String username = authentication.getName();
        return tradeService.updateTrade(id, tradeDetails, username);
    }

    // DELETE trade
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrade(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        tradeService.deleteTrade(id, username);
        return ResponseEntity.ok("Trade deleted with id: " + id);
    }

    //            STATISTICS ENDPOINTS

    // GET total P&L
    @GetMapping("/stats/total-pnl")
    public double getTotalPnL(Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getTotalPnL(username);
    }

    // GET win rate
    @GetMapping("/stats/winrate")
    public String getWinRate(Authentication authentication){
        String username = authentication.getName();
        double winRate = tradeService.getWinRate(username);
        return String.format("Win rate: %.1f%%", winRate);
    }

    // GET best trade (highest P&L)
    @GetMapping("/stats/best-trade")
    public ResponseEntity<Trade> getBestTrade(Authentication authentication){
        String username = authentication.getName();
        Trade best = tradeService.getBestTrade(username);
        if(best == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(best);
    }

    // GET worst trade (lowest P&L)
    @GetMapping("/stats/worst-trade")
    public ResponseEntity<Trade> getWorstTrade(Authentication authentication){
        String username = authentication.getName();
        Trade worst = tradeService.getWorstTrade(username);
        if(worst == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(worst);
    }

    // GET average P&L per trade
    @GetMapping("/stats/avg-pnl")
    public double getAveragePnl(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getAveragePnl(username);
    }

    // GET statistics by strategy
    @GetMapping("/stats/by-strategy")
    public List<Map<String,Object>> getStatsByStrategy(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getStatsbyStrategy(username);
    }

    // GET trades count by symbol
    @GetMapping("/stats/by-symbol")
    public List<Map<String,Object>> getTradesBySymbol(Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getCountBySymbol(username);
    }

    // GET win/loss breakdown
    @GetMapping("/stats/win-loss")
    public Map<String,Object> getWinLossBreakdown(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getWinLossBreakdown(username);
    }

    //    FILTERING ENDPOINTS

    // GET trades by strategy
    @GetMapping("/strategy/{strategy}")
    public List<Trade> getTradesByStrategy(@PathVariable String strategy, Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getTradesByStrategy(username, strategy);
    }

    //  DATE BASED ENDPOINTS

    // GET trades from specific date
    @GetMapping("/date/{date}")
    public List<Trade> getTradesByDate(@PathVariable String date, Authentication authentication){
        String username = authentication.getName();
        return tradeService.getTradesByDate(username, date);
    }

    // GET trades from date range
    @GetMapping("/date-range")
    public List<Trade> getTradesByDateRange(
            @RequestParam String start,
            @RequestParam String end,
            Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getTradesByDateRange(username, start, end);
    }

    // GET today's trades
    @GetMapping("/today")
    public List<Trade> getTodayTrades(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getTodayTrades(username);
    }

    // GET this week's trades
    @GetMapping("/this-week")
    public List<Trade> getThisWeekTrades(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getThisWeekTrades(username);
    }

    // GET this month's trades
    @GetMapping("/this-month")
    public List<Trade> getThisMonthTrades(Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getThisMonthTrades(username);
    }
}
