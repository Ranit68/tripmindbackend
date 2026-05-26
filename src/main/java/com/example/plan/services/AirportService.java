package com.example.plan.services;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class AirportService {

    private List<Map<String, Object>> airports;

    @PostConstruct
    public void loadAirports(){

        try {

            ObjectMapper mapper =
                    new ObjectMapper();

            InputStream inputStream =
                    getClass()
                    .getResourceAsStream(
                            "/airports.json"
                    );

            Map json =
                    mapper.readValue(
                            inputStream,
                            Map.class
                    );

            airports =
                    (List<Map<String, Object>>)
                            json.get("airports");

            System.out.println(
                    "Loaded Airports: "
                    + airports.size()
            );

        } catch (Exception e){

            e.printStackTrace();
        }
    }



    public Map<String, Object> getAirport(
            String city
    ){

        if(airports == null){
            return null;
        }

        for(Map<String, Object> airport : airports){

            String airportCity =
                    airport.get("city")
                            .toString();

            if(
                airportCity.equalsIgnoreCase(city)
            ){

                return airport;
            }
        }

        return null;
    }
}