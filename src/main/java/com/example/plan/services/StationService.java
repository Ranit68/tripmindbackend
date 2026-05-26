package com.example.plan.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StationService {

    @Value("${RAPID_API_KEY}")
    private String API_KEY;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://irctc1.p.rapidapi.com")
                    .defaultHeader(
                            "x-rapidapi-key",
                            API_KEY
                    )
                    .defaultHeader(
                            "x-rapidapi-host",
                            "irctc1.p.rapidapi.com"
                    )
                    .build();

public String getStationCode(String city){

    try {

        Map response = webClient.get()

                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/v1/searchStation")
                                .queryParam("query", city)
                                .build()
                )

                .retrieve()

                .onStatus(
                        status -> status.value() == 429,

                        clientResponse -> {

                            System.out.println(
                                    "Station API rate limit exceeded"
                            );

                            return reactor.core.publisher.Mono.empty();
                        }
                )

                .bodyToMono(Map.class)

                .onErrorReturn(new java.util.HashMap<>())

                .block();



        if(response == null || response.isEmpty()){

            return null;
        }

        System.out.println(response);




        Object dataObj = response.get("data");

        if(!(dataObj instanceof List)){

            return null;
        }

        List stations = (List) dataObj;



        if(stations.isEmpty()){

            return null;
        }



        Object firstObj = stations.get(0);

        if(!(firstObj instanceof Map)){

            return null;
        }

        Map firstStation = (Map) firstObj;

        System.out.println(firstStation);




        Object code = firstStation.get("station_code");

        if(code == null){

            code = firstStation.get("stationCode");
        }

        if(code == null){

            code = firstStation.get("code");
        }

        if(code == null){

            return null;
        }



        return code.toString();

    } catch (Exception e){

        System.out.println(
                "Station service failed"
        );

        e.printStackTrace();

        return null;
    }
}
}