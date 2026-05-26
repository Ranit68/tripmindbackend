package com.example.plan.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LocationService {
    
    public final WebClient webClient = WebClient.builder().baseUrl("https://geocoding-api.open-meteo.com")
    .build();

    public Map<String, Double> getCoordinates(String city){
        Map response = webClient.get().uri(UriBuilder -> UriBuilder
            .path("/v1/search").queryParam("name", city)
            .queryParam("count", 1)
            .queryParam("countryCode", "IN")
            .queryParam("language", "en")
            .queryParam("format", "json")
            .build())
            .retrieve()
            .bodyToMono(Map.class)
            .block();
            

        var results = (java.util.List<Map<String, Object>>) response.get("results");

        if(results == null || results.isEmpty()){
            throw new RuntimeException("City not found");
        }
        Map<String, Object> location = results.get(0);
        return Map.of(
            "lat", ((Number) location.get("latitude")).doubleValue(),
            "lon", ((Number) location.get("longitude")).doubleValue()

            
        );
        
    }
    
}
