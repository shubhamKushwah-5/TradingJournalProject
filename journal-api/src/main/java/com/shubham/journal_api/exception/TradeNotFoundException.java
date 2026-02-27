package com.shubham.journal_api.exception;

import com.shubham.journal_api.model.Trade;

public class TradeNotFoundException extends RuntimeException {

    public TradeNotFoundException(Long id) {
        super("Trade not found with id: " + id);
    }

    public TradeNotFoundException(String message) {
        super(message);
    }
}
