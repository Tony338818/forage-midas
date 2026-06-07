package com.jpmc.midascore;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionConsumer {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveCal incentiveCal;

    public TransactionConsumer(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveCal incentiveCal) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveCal = incentiveCal;
    }
    
    // Task 2 Consumer
    public void handleTask2(Transaction transaction){
        System.out.println("Sender: " + transaction.getSenderId() + " Reciever: " + transaction.getRecipientId() + " amount: " + transaction.getAmount());
    }
    
    // Task 3 Consumer
    public void handleTask3(Transaction transaction){
        // 1. Fetch both users safely from the database
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        
        // 2. Safety check: Ensure both accounts actually exist
        if (sender == null || recipient == null) {
            System.out.println("REJECTED: Invalid sender or recipient ID.");
            return;
        }
        
        // 3. Validation check: Prevent account balances from dropping below 0
        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("REJECTED (Insufficient Funds): " + sender.getName() + 
            " tried to send " + transaction.getAmount() + 
            " but only has " + sender.getBalance());
            return; 
        }
        
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());
        
        // 4. Persist the updated balances back to the database
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // 5. Record transaction
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setAmount(transaction.getAmount());
        transactionRecord.setSender(sender);
        transactionRecord.setRecipient(recipient);
        transactionRecord.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transactionRecord);
        
        // 6. Print something for the console
        System.out.println("person: " + sender.getName() + " balance: " + sender.getBalance());
        System.out.println("person: " + recipient.getName() + " balance: " + recipient.getBalance());
    }
    
    // Task 4 Consumer
    @KafkaListener(topics = "${general.kafka-topic}")
    public void handleTask4(Transaction transaction) {
        System.out.println("Processing ------");
        // 1. Fetch both users safely from the database
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        
        // 2. Safety check: Ensure both accounts actually exist
        if (sender == null || recipient == null) {
            System.out.println("REJECTED: Invalid sender or recipient ID.");
            return;
        }

        // 3. Validation check: Prevent account balances from dropping below 0
        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("REJECTED (Insufficient Funds): " + sender.getName() + 
                               " tried to send " + transaction.getAmount() + 
                               " but only has " + sender.getBalance());
            return; 
        }
        
        sender.setBalance(sender.getBalance() - transaction.getAmount());

        // 3. Get incentive and add it to recepient
        float incentiveAmt = incentiveCal.post(transaction);
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmt);

        // 4. Persist the updated balances back to the database
        userRepository.save(sender);
        userRepository.save(recipient);

        // 6. Record transaction
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setAmount(transaction.getAmount());
        transactionRecord.setSender(sender);
        transactionRecord.setRecipient(recipient);
        transactionRecord.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transactionRecord);

        // 7. Print something for the console
        System.out.println("person: " + sender.getName() + " balance: " + sender.getBalance());
        System.out.println("person: " + recipient.getName() + " balance: " + recipient.getBalance());
    }



}