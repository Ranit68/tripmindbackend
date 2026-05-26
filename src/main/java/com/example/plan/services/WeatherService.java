package com.example.plan.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherService {
    
    private final WebClient webClient = WebClient.builder()
    .baseUrl("https://api.open-meteo.com")
    .build();

    public Map getWeather(double latitude, double longitude, String startDate, String endDate) {

        String uri = "/v1/forecast?" +
                "latitude=" + latitude +
                "&longitude=" + longitude +
                "&start_date=" + startDate +
                "&end_date=" + endDate +
                "&daily=weathercode,temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
                "&current_weather=true" +
                "&timezone=auto";

        return webClient.get()
        .uri(uri)
        .retrieve()
        .bodyToMono(Map.class)
        .block();
    }
}
