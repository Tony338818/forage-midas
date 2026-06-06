package com.jpmc.midascore;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionConsumer {

    private final UserRepository userRepository;

    public TransactionConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void handleTransaction(Transaction transaction) {
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

        // 4. Process the transaction if it passes validation
        System.out.println("PROCESSING: " + transaction);
        
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // 5. Persist the updated balances back to the database
        userRepository.save(sender);
        userRepository.save(recipient);
    }
}