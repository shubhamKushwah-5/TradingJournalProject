package com.shubham.journal_api.repository;

import com.shubham.journal_api.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade,Long> {
    //frequently used command
    // - save()
    // - findAll()
    // - findById()
    // - deleteById()
    // - count()

    List<Trade> findByStrategy(String strategy);
    List<Trade> findBySymbol(String symbol);

    List<Trade> findByTradeDateBetween(LocalDateTime start, LocalDateTime end);

    List<Trade> findByTradeDateAfter(LocalDateTime date);

    @Query("SELECT t FROM Trade t WHERE DATE(t.tradeDate ) = CURRENT_DATE")
    List<Trade> findTodayTrades();
}
