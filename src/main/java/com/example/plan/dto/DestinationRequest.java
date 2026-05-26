package com.example.plan.dto;
import lombok.Data;

@Data
public class DestinationRequest {
    private String source;
    private String startDate;
private int budget;
private int days;
private String mood;
private String weather;
private String travelType;
    
}
