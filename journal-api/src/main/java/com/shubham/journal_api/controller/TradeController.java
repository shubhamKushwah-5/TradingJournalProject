package com.shubham.journal_api.controller;

import com.shubham.journal_api.model.Trade;
import com.shubham.journal_api.service.CsvImportService;
import com.shubham.journal_api.service.FileUploadService;
import com.shubham.journal_api.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
public class TradeController{

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private FileUploadService fileUploadService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    //                 BASIC CRUD CONTROLLERS

    // GET all trades for logged-in user
    @GetMapping
    public List<Trade> getAllTrades(Authentication authentication){
        String username = authentication.getName();
        return tradeService.getUserTrades(username);
    }

    //Paginated endpoint
    @GetMapping("/paginated")
    public Page<Trade> getTradesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            //implementing sort feature
            @RequestParam(defaultValue = "tradeDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication){

        String username = authentication.getName();
        return tradeService.getUserTradesPaginated(username,page,size,sortBy,direction);
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

    // GET statistics by strategy Paginated
    @GetMapping("/stats/by-strategy/paginated")
    public Page<Map<String,Object>> getStatsByStrategyPaginated (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        return tradeService.getStatsByStrategyPaginated(username,page,size);
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

    //Add trade with Screenshot
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Trade> addTradeWithScreenshot(
            @RequestParam("symbol") String symbol,
            @RequestParam("type") String type,
            @RequestParam("entryPrice") double entryPrice,
            @RequestParam("exitPrice") double exitPrice,
            @RequestParam("quantity") int quantity,
            @RequestParam("strategy") String strategy,
            @RequestParam(value = "screenshot", required = false)MultipartFile screenshot,
            Authentication authentication) throws IOException {

        Trade trade = new Trade(symbol,type,entryPrice,exitPrice,quantity,strategy);

        String username = authentication.getName();
        Trade saved = tradeService.addTradeWithScreenshot(trade,username,screenshot);

        return new ResponseEntity<>(saved,HttpStatus.CREATED);
    }

    //upload screenshot to existing trade
    @PostMapping("/{id}/screenshot")
    public Trade uploadScreenshot(
            @PathVariable Long id,
            @RequestParam("screenshot") MultipartFile screenshot,
            Authentication authentication) throws IOException {

        String username = authentication.getName();
        return tradeService.updateScreenshot(id,username, screenshot);
    }

    //Get screrenshot
    @GetMapping("/{id}/screenshot")
    public ResponseEntity<byte[]> getScreenshot(@PathVariable Long id, Authentication authentication) throws IOException {
        String username = authentication.getName();
        Trade trade = tradeService.getTradeById(id,username);

        if(trade.getScreenshotPath() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(uploadDir).resolve(trade.getScreenshotPath());
        byte[] image = Files.readAllBytes(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    // Import trades from CSV
    @PostMapping("/import/csv")
    public ResponseEntity<Map<String, Object>> importCsv (
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            List<Trade> imported = csvImportService.importTradesFromCsv(file,username);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Trades imported successfully");
            response.put("count", imported.size());
            response.put("trades", imported);

            return ResponseEntity.ok(response);
        }  catch (Exception e) {
            Map<String , Object> error = new HashMap<>();
            error.put("error", "CSV import failed: " + e.getMessage());
            return  ResponseEntity.badRequest().body(error);
        }
    }

    // Export trades to CSV
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCsv(Authentication authentication) {
        String username = authentication.getName();
        String csv = tradeService.exportTradesToCsv(username);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename = trades.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }



}
