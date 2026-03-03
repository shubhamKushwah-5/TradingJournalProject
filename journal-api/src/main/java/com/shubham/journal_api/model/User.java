package com.shubham.journal_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "username must be between 3 and 20 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "password cannot be empty")
    @Size(min = 6, message = "Password must be atleast 6 characters")
    @Column(nullable = false)
    private String password; //this will be hashed

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "role")
    private String role = "USER"; //Default value role

    @Column(name = "created_at",updatable= false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One user can have many trades
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Trade> trades = new ArrayList<>();

    @PrePersist
    protected  void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //constructors
    public User() {}

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password , String fullName){
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    //Getters and Setters

    public Long getId(){ return id;}
    public void setId(Long id){ this.id = id;}

    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getPassword(){ return password;}
    public void setPassword(String password){this.password= password;}

    public String getFullName(){return fullName;}
    public void setFullName(String fullName){this.fullName = fullName;}

    public String getRole(){ return role;}
    public void setRole(String role){this.role = role;}

    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
    public List<Trade> getTrades(){ return trades;}
    public void setTrades(List<Trade> trades){
        this.trades = trades;
    }
}
