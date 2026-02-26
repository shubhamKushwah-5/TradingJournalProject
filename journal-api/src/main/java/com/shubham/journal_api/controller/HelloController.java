package com.shubham.journal_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String homepage(){
        return "Trading Journal API is running" ;
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello from Spring Boot!" ;
    }

    @GetMapping("/greet/{name}")
    public String greet(@PathVariable("name") String name) {
        return "Hello, " + name + "! Welcome to Trading Journal.";
    }
}
