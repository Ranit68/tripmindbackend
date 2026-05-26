package com.example.plan.services;


import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RouteService {
    
    @Value("${RAPID_API_KEY}")
    private String API_KEY;
    
    private final ExchangeStrategies strategies =
        ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024)
                )
                .build();

    private final WebClient webClient = WebClient.builder()
    .baseUrl("https://api.openrouteservice.org")
    .defaultHeader(HttpHeaders.AUTHORIZATION, API_KEY)
    .exchangeStrategies(strategies)
    .build();

    public Map getDrivingRoute(
        double sourceLat,
        double sourceLng,
        double destLat,
        double destLng
    ){
        String uri = "/v2/directions/driving-car?" + "start=" + sourceLng + "," + sourceLat +
        "&end=" + destLng + "," + destLat + "&instructions=false" + "&geometry=false" + "&elevation=false";

        return webClient.get()
        .uri(uri)
        .retrieve()
        .bodyToMono(Map.class)
        .block();
    }
}
