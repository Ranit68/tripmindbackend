package com.example.plan.services;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class FlightService {
    
    public Map<String, Object> getFlightRoute(
        String source,
        String destination
    ){
        return Map.of(
                "fromAirport", "DEL",
                "toAirport", "GOI",
                "airline", "IndiGo",
                "duration", "2h 45m",

                "sourceCoords", Map.of(
                        "lat", 28.5562,
                        "lng", 77.1000
                ),

                "destCoords", Map.of(
                        "lat", 15.3800,
                        "lng", 73.8314
                )
        );
    }
}
