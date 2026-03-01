package com.shubham.journal_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


@Entity //this tells jpa that this is a database table
@Table(name= "trades") //table name in mysql db
public class Trade {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment
    private Long id;

    @NotBlank(message = "symbol cannot be empty")
    @Size(min = 2 , max = 20, message = "symbol must be between 2 and 20 characters")
    @Column(nullable = false)
    private String symbol;

    @NotBlank(message = "Type cannot be empty")
    @Pattern(regexp = "BUY|SELL", message = "Type must be either BUY or SELL")
   // @Column(nullable = false)
    private String type ; //BUY OR SELL

    @Positive(message = "Entry price must be positive")
    @Column(name = "entry_price")
    private double entryPrice;

    @Positive(message = "Exit price must be positive")
    @Column(name = "exit_price")
    private double exitPrice;

    @Positive(message = "Quantity must be positive")
    private int quantity;

    @NotBlank(message = "Strategy cannot be empty")
    private String strategy;

    //time fields
    @Column(name = "trade_date")
    private LocalDateTime tradeDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    //automatically set timestamps
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if(tradeDate ==null){
            tradeDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }



    //getter and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() {return symbol;}
    public void setSymbol(String symbol) {this.symbol = symbol;}

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public double getEntryPrice() {return entryPrice;}
    public void setEntryPrice(double entryPrice) {this.entryPrice = entryPrice;}

    public double getExitPrice() {return exitPrice;}
    public void setExitPrice(double exitPrice) {this.exitPrice = exitPrice;}

    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

    public String getStrategy() {return strategy;}
    public void setStrategy(String strategy) {this.strategy = strategy;}

    public LocalDateTime getTradeDate() {return tradeDate;}
    public void setTradeDate(LocalDateTime tradeDate) {this.tradeDate = tradeDate;}

    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt ;}

    public LocalDateTime getUpdateAt() { return updateAt;}
    public void setUpdateAt(LocalDateTime updateAt){this.updateAt = updateAt;}


//    tradeDate = when trade actually happened
//    createdAt = when record was created in DB
//    updatedAt = when record was last modified
//    @PrePersist = runs before saving to DB
//    @PreUpdate = runs before updating in DB

    //constructors
    public Trade() {
    }

    public Trade(String symbol, String type, double entryPrice,
                 double exitPrice, int quantity, String strategy) {
        this.symbol = symbol;
        this.type = type;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.quantity = quantity;
        this.strategy = strategy;
    }

    //constructor with date
    public Trade(String symbol, String type,double entryPrice,
                 double exitPrice, int quantity, String strategy,
                 LocalDateTime tradeDate){
        this.symbol = symbol;
        this.type = type ;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.quantity = quantity;
        this.strategy = strategy;
        this.tradeDate = tradeDate;
    }

    //methods(not stored in database mysql
    public double calculatePnL(){
        if(type.equals("BUY")) {
            return (exitPrice-entryPrice) * quantity;
        }
        return (entryPrice - exitPrice) * quantity;

    }



}
