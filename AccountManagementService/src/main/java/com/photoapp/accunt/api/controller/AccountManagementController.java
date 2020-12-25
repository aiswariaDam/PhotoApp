package com.photoapp.accunt.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountManagementController {
    @GetMapping("/details")
    public String getAccountDetails(){
    return "Account Details";
    }
}
