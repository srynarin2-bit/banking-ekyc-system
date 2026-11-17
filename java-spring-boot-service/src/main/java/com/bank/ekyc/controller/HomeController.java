package com.bank.ekyc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "🚀 ABA eKYC Banking Backend System is Live and Running Successfully!";
    }
}