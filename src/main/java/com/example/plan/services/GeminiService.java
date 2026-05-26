package com.example.plan.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.plan.dto.DestinationRequest;
import com.example.plan.dto.TripRequest;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String API_KEY;

    private final TransportAggregatorService aggregatorService;

    public GeminiService(
            TransportAggregatorService aggregatorService
    ) {
        this.aggregatorService = aggregatorService;
    }

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public String suggestDest(DestinationRequest request) {

        String prompt = """
                Generate ONLY valid JSON.

                Do not add markdown.
                Do not add explanation.
                Do not add ```json.

                IMPORTANT RULES:
                - Each suggestion must contain ONLY ONE destination/city.
                - Never combine multiple cities in one suggestion.
                - Never use '&', '/', 'and'.
                - Return one city per card only.

                Return this structure:

                {
                  "suggestions": [
                    {
                      "destination": {
                        "city": "",
                        "state": "",
                        "country": ""
                      },

                      "reason": "",

                      "estimatedBudget": 0,

                      "weather": "",

                      "bestFor": "",

                      "hotelEstimate": "",

                      "travelEstimate": ""
                    }
                  ]
                }

                User Details:

                Starting City: %s
                Budget: ₹%d
                Days: %d
                Mood: %s
                Weather Preference: %s
                Travel Type: %s

                Suggest 5 destinations.
                """.formatted(
                request.getSource(),
                request.getBudget(),
                request.getDays(),
                request.getMood(),
                request.getWeather(),
                request.getTravelType()
        );

        return callGemini(prompt);
    }

    public String generateTripPlan(TripRequest request) {

        Map<String, Object> transportData =
                aggregatorService.aggregate(request);

        Map flightData =
                (Map) transportData.get("flight");

        Map trainData =
                (Map) transportData.get("train");

        Map roadData =
                (Map) transportData.get("road");

        Map weatherData =
                (Map) transportData.get("weather");

        Map currentWeather =
        new java.util.HashMap();

if(
    weatherData != null &&
    weatherData.containsKey("current_weather")
){

    Object currentObj =
            weatherData.get("current_weather");

    if(currentObj instanceof Map){

        currentWeather = (Map) currentObj;
    }
}

        Map daily = new java.util.HashMap();

if(
    weatherData != null &&
    weatherData.containsKey("daily")
){

    Object dailyObj =
            weatherData.get("daily");

    if(dailyObj instanceof Map){

        daily = (Map) dailyObj;
    }
}

        Object maxTemps =
        daily.getOrDefault(
                "temperature_2m_max",
                "Unavailable"
        );

Object minTemps =
        daily.getOrDefault(
                "temperature_2m_min",
                "Unavailable"
        );

Object rainProb =
        daily.getOrDefault(
                "precipitation_probability_max",
                "Unavailable"
        );

        String prompt = """
Generate ONLY valid JSON.

Do not add markdown.
Do not add explanation.
Do not add ```json.

Create a travel plan with this structure:

{
  "destination": "",

  "totalBudget": 0,

  "weather": {

    "currentTemp": 0,

    "condition": "",

    "forecast": [
      {
        "day": "",
        "maxTemp": 0,
        "minTemp": 0,
        "rainChance": 0
      }
    ]
  },

  "safetyAlerts": [],

  "packingSuggestions": [],

  "transportOptions": {

    "flight": {},

    "train": {},

    "road": {}
  },

  "hotels": [],
  "optionalExperiences": [
  {
    "name": "",
    "extraCost": 0,
    "category": "",
    "reason": "",
    "bestTime": ""
  }
],

  "itinerary": [
        {
        "day": 1,
        "title": "",
        "activities": [
            {
    "name": "",
    "estimatedCost": 0,
    "category": "",
    "time": "",
    "description": ""
  }
        ],
        "estimatedCost": {
      "breakfast": 0,
      "lunch": 0,
      "dinner": 0,
      "hotel": 0,
      "transport": 0,
      "tickets": 0,
      "shopping": 0,
      "other": 0,
      "dailyTotal": 0 
        }
  ]
}

IMPORTANT ACTIVITY RULES:

- Analyze remaining budget carefully.
- If extra budget is available, include:
  adventure activities,
  sightseeing,
  local experiences,
  cultural activities,
  nightlife,
  trekking,
  water sports,
  snow activities,
  safari,
  amusement activities,
  boating,
  rafting,
  scuba diving,
  paragliding,
  bungee jumping,
  camping,
  zipline,
  desert safari,
  local food tours.

- Activities must match destination type.
- Activities must fit remaining budget.
- Never exceed total user budget.
- Include realistic activity pricing.
- Include entry ticket cost if needed.
- Prioritize popular experiences of destination.

EXTRA EXPERIENCE RULES:

- If user budget does NOT allow premium activities,
  suggest them separately as optional upgrades.

- Optional upgrades must:
  include extra cost required,
  explain why activity is worth it,
  remain destination-specific.

- Label them as:
  "optionalExperiences"

- These activities must NOT be included in final trip cost.

- Mention:
  "Requires extra budget"

- Include:
  activity name,
  extra required amount,
  category,
  reason,
  recommended timing.

IMPORTANT BUDGET RULES:

- Entire trip MUST stay inside total budget.
- Never exceed user's budget.
- Lower budget = budget hotels and cheap transport.
- Higher budget = premium options allowed.
- Every itinerary day MUST include estimated spending.
- Show realistic India pricing.
- Include:
  breakfast
  lunch
  dinner
  hotel
  local transport
  tickets
  activities
  shopping
  misc expenses

- Add daily total.
- Add final total trip cost.
- Final total cost should be <= user budget.
At the end include:

"budgetSummary": {
   "userBudget": 0,
   "estimatedTripCost": 0,
   "remainingBudget": 0
}


CURRENT WEATHER:

Temperature: %s°C

Wind Speed: %s km/h



FORECAST:

Max Temp: %s

Min Temp: %s

Rain Probability: %s



REAL TRANSPORT DATA:

Flight Data:
%s

Train Data:
%s

Road Route Data:
%s



IMPORTANT RULES:

- Use ONLY provided transport data.
- Do NOT invent routes.
- Do NOT invent prices.
- Do NOT invent train names.
- Do NOT invent travel duration.
- Use ONLY provided weather data.
- Do NOT invent temperatures.



GENERATE:

- weather warnings
- packing suggestions
- rain alerts
- safety alerts
- best outdoor timings
- mountain safety warnings
- landslide warnings
- snow warnings
- trekking suitability
- safest transport option
- cheapest transport option
- fastest transport option



USER DETAILS:

Source: %s

Destination: %s

Budget: ₹%d

Days: %d

Mood: %s



HOTELS MUST INCLUDE:
- hotelName
- pricePerNight
- location



ITINERARY MUST BE DAY-WISE.
""".formatted(

               currentWeather.getOrDefault(
        "temperature",
        "Unavailable"
),
                currentWeather.getOrDefault(
        "windspeed",
        "Unavailable"
),

                maxTemps,
                minTemps,
                rainProb,

                flightData,
                trainData,
                roadData,

                request.getSource(),
                request.getDestination(),
                request.getBudget(),
                request.getDays(),
                request.getMood()
        );

        return callGemini(prompt);
    }

    private String callGemini(String prompt) {

        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        return webClient.post()
                .uri("/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY)

                .contentType(MediaType.APPLICATION_JSON)

                .bodyValue(body)

                .retrieve()

                .onStatus(
                        status -> status.value() == 429,
                        response -> {
                            throw new RuntimeException(
                                    "Gemini API rate limit exceeded. Please wait a few seconds."
                            );
                        }
                )

                .bodyToMono(Map.class)

                .map(response -> {

                    var candidates =
                            (java.util.List<?>) response.get("candidates");

                    if (candidates == null || candidates.isEmpty()) {
                        return "No AI response";
                    }

                    var candidate =
                            (Map<?, ?>) candidates.get(0);

                    var content =
                            (Map<?, ?>) candidate.get("content");

                    var parts =
                            (java.util.List<?>) content.get("parts");

                    var part =
                            (Map<?, ?>) parts.get(0);

                    return part.get("text").toString();
                })

                .block();
    }
}