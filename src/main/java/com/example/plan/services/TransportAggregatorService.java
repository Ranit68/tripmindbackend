package com.example.plan.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.plan.dto.TripRequest;

@Service
public class TransportAggregatorService {

    private final FlightService flightService;
    private final TrainService trainService;
    private final RouteService routeService;
    private final LocationService locationService;
    private final StationService stationService;
    private final WeatherService weatherService;

    public TransportAggregatorService(
            FlightService flightService,
            TrainService trainService,
            RouteService routeService,
            LocationService locationService,
            StationService stationService,
            WeatherService weatherService
    ) {

        this.flightService = flightService;
        this.trainService = trainService;
        this.routeService = routeService;
        this.locationService = locationService;
        this.stationService = stationService;
        this.weatherService = weatherService;
    }

    public Map<String, Object> aggregate(TripRequest request){

        LocalDate start =
                LocalDate.parse(request.getStartDate());

        LocalDate end =
                start.plusDays(request.getDays());


        Map<String, Double> sourceCoords =
                locationService.getCoordinates(
                        request.getSource()
                );

        Map<String, Double> destCoords =
                locationService.getCoordinates(
                        request.getDestination()
                );

        Map weatherData = new HashMap<>();

        try {
             weatherData = weatherService.getWeather(
                        destCoords.get("lat"),
                        destCoords.get("lon"),
                        start.toString(),
                        end.toString()
                );
            } catch(Exception e){
                System.out.println("Weather api failed");
                e.printStackTrace();
            }


                Map<String, Object> flightData =
        new HashMap<>();

        try {
        flightData =
                flightService.getFlightRoute(
                        request.getSource(),
                        request.getDestination()
                );
            } catch (Exception e){
                System.out.println("flight api failed");
            }

        Map<String, Object> simplifiedFlight = new HashMap<>();
        simplifiedFlight.put("airline", flightData.get("airline"));
        simplifiedFlight.put(
    "duration",
    flightData.get("duration")
);

simplifiedFlight.put(
    "price",
    flightData.get("price")
);

simplifiedFlight.put(
    "fromAirport",
    flightData.get("fromAirport")
);

simplifiedFlight.put(
    "toAirport",
    flightData.get("toAirport")
);
Map roadData =
        new HashMap<>();

try { roadData =
                routeService.getDrivingRoute(
                        sourceCoords.get("lat"),
                        sourceCoords.get("lon"),
                        destCoords.get("lat"),
                        destCoords.get("lon")
                );
            } catch(Exception e){
                System.out.println("Road api failed");
                e.printStackTrace();
            }

        Map simplifiedRoad = new HashMap();

        try {

            Map feature =
                    (Map) ((List) roadData.get("features"))
                            .get(0);

            Map properties =
                    (Map) feature.get("properties");

            Map summary =
                    (Map) properties.get("summary");

           simplifiedRoad = new HashMap();

simplifiedRoad.put(
    "distanceKm",
    ((Double) summary.get("distance")) / 1000
);

simplifiedRoad.put(
    "durationHours",
    ((Double) summary.get("duration")) / 3600
);

        } catch (Exception e){
            e.printStackTrace();
        }

        String fromStation =null;

        String toStation =null;

        Map trainData = new HashMap<>();
        try{
            fromStation =
            stationService.getStationCode(
                    request.getSource()
            );

    toStation =
            stationService.getStationCode(
                    request.getDestination()
            );
                trainData = trainService.searchTrains(
                        fromStation,
                        toStation,
                        request.getStartDate()
                );
            } catch(Exception e){
                System.out.println("traindata failed");
                e.printStackTrace();
            }

        Map simplifiedTrain = new HashMap();

        try {

            List trains =
                    (List) trainData.get("data");

            if(trains != null && !trains.isEmpty()){

                Map firstTrain =
                        (Map) trains.get(0);

                simplifiedTrain = new HashMap();

simplifiedTrain.put(
    "trainName",
    firstTrain.get("train_name")
);

simplifiedTrain.put(
    "trainNumber",
    firstTrain.get("train_number")
);

simplifiedTrain.put(
    "departure",
    firstTrain.get("from_std")
);

simplifiedTrain.put(
    "arrival",
    firstTrain.get("to_std")
);

simplifiedTrain.put(
    "duration",
    firstTrain.get("duration")
);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();

result.put("flight", simplifiedFlight);
result.put("road", simplifiedRoad);
result.put("train", simplifiedTrain);
result.put("weather", weatherData);
result.put("sourceCoords", sourceCoords);
result.put("destCoords", destCoords);

return result;
    }
}