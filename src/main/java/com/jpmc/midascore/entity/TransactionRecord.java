package com.jpmc.midascore.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TransactionRecord {
    
    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name= "sender_id")
    private UserRecord sender;

    public UserRecord getSender() {
        return sender;
    }
    public void setSender(UserRecord sender) {
        this.sender = sender;
    }

    @ManyToOne
    @JoinColumn(name= "recipient_id")
    private UserRecord recipient;

    public UserRecord getRecipient() {
        return recipient;
    }
    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }

    @Column(nullable = false)
    private float amount;

    public float getAmount() {
        return amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }

    private LocalDateTime createdAt;
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
