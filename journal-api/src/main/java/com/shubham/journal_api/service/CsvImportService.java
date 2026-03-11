package com.shubham.journal_api.service;

import com.shubham.journal_api.model.Trade;
import com.shubham.journal_api.model.User;
import com.shubham.journal_api.repository.TradeRepository;
import com.shubham.journal_api.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Trade> importTradesFromCsv(MultipartFile file, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Trade> trades = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());

        for (CSVRecord record : csvParser) {
            Trade trade = new Trade();

            //Map CSV columns to Trade fields
            trade.setSymbol(record.get("symbol"));
            trade.setType(record.get("type"));
            trade.setEntryPrice(Double.parseDouble(record.get("entry_price")));
            trade.setExitPrice(Double.parseDouble(record.get("exit_price")));
            trade.setQuantity(Integer.parseInt(record.get("quantity")));
            trade.setStrategy(record.get("strategy"));

            // Parse date if exists
            if(record.isMapped("trade_date") && !record.get("trade_date").isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                trade.setTradeDate(LocalDateTime.parse(record.get("trade_date"), formatter));
            }

            trade.setUser(user);
            trades.add(trade);
        }

        // Save all trades
        return tradeRepository.saveAll(trades);
    }
}
