package com.example.plan.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TrainService {
    @Value("${RAPID_API_KEY}")
    private String API_KEY;

    private final WebClient webClient = WebClient.builder()
    .baseUrl("https://irctc1.p.rapidapi.com")
    .defaultHeader("x-rapidapi-key", API_KEY)
    .defaultHeader("x-rapidapi-host", "irctc1.p.rapidapi.com")
    .build();


    public Map searchTrains(
        String from,
        String to,
        String journeyDate
    ){
        return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/api/v3/trainBetweenStations")
            .queryParam("fromStationCode", from)
            .queryParam("toStationCode", to)
        .queryParam("dateOfJourney", journeyDate)
        .build()
        )
        .retrieve()
        .bodyToMono(Map.class)
        .block();
    }
}
