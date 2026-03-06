package com.shubham.journal_api.service;

import com.shubham.journal_api.exception.TradeNotFoundException;
import com.shubham.journal_api.model.Trade;
import com.shubham.journal_api.model.User;
import com.shubham.journal_api.repository.TradeRepository;
import com.shubham.journal_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private UserRepository userRepository;

    //                BASIC CRUD OPERATIONS

    //Get all trades for a specific user
    public List<Trade> getUserTrades(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getTrades();
    }

    //Add trade for a specific user
    public Trade addTrade(Trade trade, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        trade.setUser(user); //link trade to user
        return tradeRepository.save(trade);
    }

    //Get single trade (check if it belongs to user)
    public Trade getTradeById(Long id , String username){
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new TradeNotFoundException(id));

        //security check: trade belongs to this user or not
        if(!trade.getUser().getUsername().equals(username)){
            throw new RuntimeException("Access denied: This trade doesn't belong to you ");
        }
        return trade;
    }

    //Update trade(only if belongs to user)
    public Trade updateTrade(Long id, Trade tradeDetails, String username){
        Trade trade = getTradeById(id,username); //already checked ownership in getTrade method above

        trade.setSymbol(tradeDetails.getSymbol());
        trade.setType(tradeDetails.getType());
        trade.setEntryPrice(tradeDetails.getEntryPrice());
        trade.setExitPrice(tradeDetails.getExitPrice());
        trade.setQuantity(tradeDetails.getQuantity());
        trade.setStrategy(tradeDetails.getStrategy());

        return tradeRepository.save(trade);
    }

    //Delete trade (only if belongs to user
    public void deleteTrade(Long id , String username){
        Trade trade = getTradeById(id, username); //checks ownership
        tradeRepository.delete(trade);
    }

    //           STATISTICS METHODS

    //Statistics for user's trades only
    public double getTotalPnL(String username){
        List<Trade> userTrades= getUserTrades(username);
        return userTrades.stream()
                .mapToDouble(Trade::calculatePnL)
                .sum();
    }

    public double getWinRate(String username){
        List<Trade> userTrades = getUserTrades(username);
        if (userTrades.isEmpty()) return 0;

        long wins = userTrades.stream()
                .filter(t -> t.calculatePnL() > 0)
                .count();

        return (wins * 100.0) /userTrades.size();
    }

    //Get best trade (highest P&l)
    public Trade getBestTrade(String username){
        List<Trade> trades = getUserTrades(username);
        if(trades.isEmpty()) return null;

        return trades.stream()
                .max((t1,t2) -> Double.compare(t1.calculatePnL(), t2.calculatePnL()))
                .orElse(null);
    }

    //Get worst trade(lowest pnl)
    public Trade getWorstTrade(String username) {
        List<Trade> trades = getUserTrades(username);
        if(trades.isEmpty()) return null;

        return trades.stream()
                .min((t1,t2) -> Double.compare(t1.calculatePnL(),t2.calculatePnL()))
                .orElse(null);
    }

    // Average p&l per trade
    public double getAveragePnl(String username) {
        List<Trade> trades = getUserTrades(username);
        if(trades.isEmpty()) return 0;

        return trades.stream()
                .mapToDouble(Trade::calculatePnL)
                .average()
                .orElse(0);
     }

     //                    FILTERING METHODS

    //Get trades by strategy
    public List<Trade> getTradesByStrategy(String username, String strategy) {
        List<Trade> userTrade = getUserTrades(username);
        return userTrade.stream()
                .filter(t -> t.getStrategy().equalsIgnoreCase(strategy))
                .collect(Collectors.toList());
    }

    //Get trades by symbol
    public List<Trade> getTradesBySymbol(String username, String symbol) {
        List<Trade> userTrades = getUserTrades(username);
        return userTrades.stream()
                .filter(t -> t.getSymbol().equalsIgnoreCase(symbol))
                .collect(Collectors.toList());
    }


    //             ADVANCED STATISTICS

    //statistics by strategy
    public List<Map<String , Object>> getStatsbyStrategy(String username){
        List<Trade> allTrades = getUserTrades(username);

        //Group by strategy
        Map<String , List<Trade>> byStrategy = allTrades.stream()
                .collect(Collectors.groupingBy(Trade::getStrategy));

        //Calculate Stats for each strategy
        List<Map<String ,Object>> result = new ArrayList<>();

        for(Map.Entry<String,List<Trade>> entry : byStrategy.entrySet()){
            String strategy = entry.getKey();
            List<Trade> trades = entry.getValue();

            double totalPnl = trades.stream()
                    .mapToDouble(Trade::calculatePnL)
                    .sum();

            long wins = trades.stream()
                    .filter(t -> t.calculatePnL() > 0)
                    .count();

            double winRate = (wins *100.0) /trades.size();

            Map<String,Object> stats = new HashMap<>();
            stats.put("Strategy", strategy);
            stats.put("totalTrades", trades.size());
            stats.put("totalPnl", totalPnl);
            stats.put("winRate", winRate);
            stats.put("avgPnl", totalPnl/trades.size());

            result.add(stats);
        }
        return result;
    }

    //count trades by symbol
    public List<Map<String ,Object>> getCountBySymbol(String username){
        List<Trade> allTrades = getUserTrades(username);

        Map<String , Long> countBySymbol = allTrades.stream()
                .collect(Collectors.groupingBy(Trade::getSymbol,Collectors.counting()));

        List<Map<String,Object>> result = new ArrayList<>();
        for(Map.Entry<String,Long> entry : countBySymbol.entrySet()) {
            Map<String, Object> symbolData = new HashMap<>();
            symbolData.put("symbol", entry.getKey());
            symbolData.put("count", entry.getValue());
            result.add(symbolData);
        }

        return result;
    }

    // Win/loss Breakdown
    public Map<String,Object> getWinLossBreakdown(String username) {
        List<Trade> trades = getUserTrades(username);

        if(trades.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("totalTrades",0);
            empty.put("wins",0);
            empty.put("losses", 0);
            empty.put("winRate", 0);
            return empty;
        }

        long wins = trades.stream()
                .filter(t -> t.calculatePnL() >0)
                .count();

        long losses = trades.size() - wins;

        double totalWinPnl = trades.stream()
                .filter(t -> t.calculatePnL() >0)
                .mapToDouble(Trade::calculatePnL)
                .sum();

        double totalLossPnl = trades.stream()
                .filter(t -> t.calculatePnL() <=0)
                .mapToDouble(Trade::calculatePnL)
                .sum();

        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("totalTrades", trades.size());
        breakdown.put("wins",wins);
        breakdown.put("losses", losses);
        breakdown.put("winRate", wins * 100.0 / trades.size());
        breakdown.put("totalWinPnl", totalWinPnl);
        breakdown.put("totalLossPnl", totalLossPnl);
        breakdown.put("avgWin", wins>0?totalWinPnl /wins:0);
        breakdown.put("avgLoss",losses>0?totalLossPnl/losses:0);

        return breakdown;
    }

    //                     DATE BASED FILTERING

    //Get trades by specific date
    public List<Trade> getTradesByDate(String username, String date){
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime enOfDay = localDate.plusDays(1).atStartOfDay();

        List<Trade> userTrades = getUserTrades(username);
        return userTrades.stream()
                .filter(t -> t.getTradeDate().isAfter(startOfDay) &&
                              t.getTradeDate().isBefore(enOfDay))
                .collect(Collectors.toList());
    }

    //Get trades by date range
    public List<Trade> getTradesByDateRange(String username, String start, String end) {
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).plusDays(1).atStartOfDay();

        List<Trade> userTrades = getUserTrades(username);
        return userTrades.stream()
                .filter(t -> t.getTradeDate().isAfter(startDate) &&
                             t.getTradeDate().isBefore(endDate))
                .collect(Collectors.toList());
    }

    //Get today's trades
    public List<Trade> getTodayTrades(String username){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Trade> userTrades = getUserTrades(username);
        return userTrades.stream()
                .filter(t -> t.getTradeDate().isAfter(startOfDay) &&
                             t.getTradeDate().isBefore(endOfDay))
                .collect(Collectors.toList());
    }

    //Get this week's trades
    public List<Trade> getThisWeekTrades(String username){
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        List<Trade> userTrades = getUserTrades(username);
        return userTrades.stream()
                .filter(t -> t.getTradeDate().isAfter(weekAgo))
                .collect(Collectors.toList());
    }

    //Get this month's trades
    public List<Trade> getThisMonthTrades(String username) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);

        List<Trade> userTrades = getUserTrades(username);
        return userTrades.stream()
                .filter(t->t.getTradeDate().isAfter(monthAgo))
                .collect(Collectors.toList());
    }

}
