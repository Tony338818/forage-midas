package com.jpmc.midascore;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.foundation.Incentivedto;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class IncentiveCal {
    private final RestTemplate restTemplate;

    public IncentiveCal(RestTemplateBuilder restTemplate){
        this.restTemplate = restTemplate.build();
    }

    public float post(Transaction transaction){
        String url = "http://localhost:8080/incentive";
        Incentivedto result = restTemplate.postForObject(url, transaction, Incentivedto.class);

        float amount = result.getAmount();
        return amount;
    }
    
}
