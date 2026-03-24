package com.shubham.journal_api.repository;

import com.shubham.journal_api.model.Trade;
import com.shubham.journal_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //find trades by user with pagination
    Page<Trade> findByUser(User user, Pageable pageable);

    //original code
    //@Query("SELECT t FROM Trade t WHERE DATE(t.tradeDate ) = CURRENT_DATE")
    //List<Trade> findTodayTrades();

    //new logic (works on mysql and postgreSql)
    @Query("SELECT t FROM Trade t WHERE t.tradeDate = CURRENT_DATE")
    List<Trade> findTodayTrades();
}
