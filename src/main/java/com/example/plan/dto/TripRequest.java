package com.example.plan.dto;
import lombok.Data;

@Data
public class TripRequest {
    private String startDate;
    private String source;
    private String destination;
    private int budget;
    private int days;
    private String mood;
}
