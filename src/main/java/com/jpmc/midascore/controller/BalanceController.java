package com.jpmc.midascore.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class BalanceController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        Optional<UserRecord> userRecord = userRepository.findById(userId);
        Balance balance = new Balance();

        if(userRecord.isEmpty()){
            balance.setAmount(0);
            return balance;
        }

        UserRecord user = userRecord.get();

        balance.setAmount(user.getBalance());

        return balance;
    }
    
}
