package com.example.plan.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.plan.dto.DestinationRequest;
import com.example.plan.dto.TripRequest;
import com.example.plan.services.GeminiService;


@RestController
@RequestMapping("/api/trip")
@CrossOrigin("*")
public class TripController {

    private final GeminiService geminiService;
    public TripController(GeminiService geminiService){
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public String generateTrip(@RequestBody TripRequest request) {
        
        return geminiService.generateTripPlan(request);
    }

    @PostMapping("/suggest-dest")
    public String suggestDest(@RequestBody DestinationRequest request) {
        
        return geminiService.suggestDest(request);
    }
    
    
}
