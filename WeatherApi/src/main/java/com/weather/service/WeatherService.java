package com.weather.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> getForecast(String city, String state, String country) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> locationDetails = new HashMap<>();
        Map<String, Object> weatherReport = new HashMap<>();
        Map<String, Object> dailyForecastsMap = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(headers);
        // TODO: Get location details and coordinates
        URI locationUri = UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search").queryParam("city", city)
            .queryParam("state", state).queryParam("country", country).queryParam("addressdetails", 1).queryParam("format", "json").build()
            .toUri();

        JsonNode locationResponseList = restTemplate.exchange(locationUri, HttpMethod.GET, requestEntity, JsonNode.class).getBody();
        JsonNode locationResponse = locationResponseList.get(0);
        log.info("Location Response: {}", locationResponse.toPrettyString());
        String latitude = locationResponse.get("lat").asText();
        String longitude = locationResponse.get("lon").asText();

        JsonNode address = locationResponse.get("address");
        locationDetails.put("EnglishName", address.get("city"));
        locationDetails.put("AdministrativeArea", Map.of("EnglishName", address.get("state")));
        locationDetails.put("Country", Map.of("EnglishName", address.get("country_code")));

        resultMap.put("locationDetails", locationDetails);

        // TODO: Get Weather Details
        Map<String, Object> weatherParam = new HashMap<>();
        weatherParam.put("latitude", latitude);
        weatherParam.put("longitude", longitude);
        URI weatherUri = UriComponentsBuilder.fromUriString("https://api.weather.gov/points/{latitude},{longitude}")
            .buildAndExpand(weatherParam).toUri();

        log.info("URI: {}", weatherUri);
        JsonNode weatherResponse = restTemplate.exchange(weatherUri, HttpMethod.GET, requestEntity, JsonNode.class).getBody();
        log.info("Weather Response: {}", weatherResponse.toPrettyString());

        JsonNode weatherProperties = weatherResponse.get("properties");
        Map<String, Object> forecastParam = new HashMap<>();
        forecastParam.put("wfo", weatherProperties.get("gridId").asText());
        forecastParam.put("x", weatherProperties.get("gridX").asText());
        forecastParam.put("y", weatherProperties.get("gridY").asText());

        URI forecastUri = UriComponentsBuilder.fromUriString("https://api.weather.gov/gridpoints/{wfo}/{x},{y}/forecast")
            .buildAndExpand(forecastParam).toUri();

        HttpHeaders forecastHeaders = new HttpHeaders();
        forecastHeaders.setContentType(MediaType.APPLICATION_JSON);
        forecastHeaders.add("Feature-Flags", "forecast_temperature_qv,forecast_wind_speed_qv");

        HttpEntity<JsonNode> forecastRequestEntity = new HttpEntity<>(forecastHeaders);
        JsonNode forecastResponse = restTemplate.exchange(forecastUri, HttpMethod.GET, forecastRequestEntity, JsonNode.class).getBody();
        log.info("Forecast Response: {}", forecastResponse.toPrettyString());

        JsonNode forecastPeriods = forecastResponse.get("properties").get("periods");
        JsonNode firstPeriod = forecastPeriods.get(0);
        JsonNode secondPeriod = forecastPeriods.get(1);

        Integer maxTemp = Math.max(firstPeriod.get("temperature").get("value").asInt(),
            secondPeriod.get("temperature").get("value").asInt());
        Integer minTemp = Math.min(firstPeriod.get("temperature").get("value").asInt(),
            secondPeriod.get("temperature").get("value").asInt());
        String temperatureUnit = "wmoUnit:degC".equals(firstPeriod.get("temperature").get("unitCode").asText()) ? "C" : "F";
        dailyForecastsMap.put("Temperature", Map.of("Maximun", Map.of("value", maxTemp, "Unit", temperatureUnit), "Minimum",
            Map.of("value", minTemp, "Unit", temperatureUnit)));

        Integer windSpeed = firstPeriod.get("windSpeed").has("maxValue") ? firstPeriod.get("windSpeed").get("maxValue").asInt()
            : firstPeriod.get("windSpeed").get("value").asInt();
        dailyForecastsMap.put("Daily", Map.of("Wind", Map.of("Speed", Map.of("Value", windSpeed, "Unit", "km/h"))));

        // TODO: calculate heating and cooling
        Integer tavg = (maxTemp + minTemp) / 2;
        Integer heating = Math.max(0, 18 - tavg);
        Integer cooling = Math.max(0, tavg - 18);
        dailyForecastsMap.put("DegreeDaySummary", Map.of("Cooling", Map.of("Value", cooling, "Unit", temperatureUnit), "Heating",
            Map.of("Value", heating, "Unit", temperatureUnit)));

        // TODO: get Air Quality data

        URI airQualityUri = UriComponentsBuilder.fromUriString("https://air-quality-api.open-meteo.com/v1/air-quality")
            .queryParam("latitude", latitude).queryParam("longitude", longitude).queryParam("current", "pm10,pm2_5,carbon_monoxide,ozone")
            .build().toUri();

        JsonNode aqResponse = restTemplate.exchange(airQualityUri, HttpMethod.GET, requestEntity, JsonNode.class).getBody();
        List<Map<String, Object>> airQualityList = new ArrayList<>();

        JsonNode airUnits = aqResponse.get("current_units");
        JsonNode currAir = aqResponse.get("current");
        // Fine Particulate Matter, Carbon Monoxide, Ozone
        airQualityList.add(Map.of("Name", "Particulate Matter", "Category", getCategory(currAir, "pm10"), "CategoryValue",
            currAir.get("pm10"), "Units", airUnits.get("pm10")));
        airQualityList.add(Map.of("Name", "Fine Particulate Matter", "Category", getCategory(currAir, "pm2_5"), "CategoryValue",
            currAir.get("pm2_5"), "Units", airUnits.get("pm2_5")));
        airQualityList.add(Map.of("Name", "Carbon Monoxide", "Category", getCategory(currAir, "carbon_monoxide"), "CategoryValue",
            currAir.get("carbon_monoxide"), "Units", airUnits.get("carbon_monoxide")));
        airQualityList.add(Map.of("Name", "Ozone", "Category", getCategory(currAir, "ozone"), "CategoryValue", currAir.get("ozone"),
            "Units", airUnits.get("ozone")));
        dailyForecastsMap.put("AirAndPollen", airQualityList);

        // TODO: get astonomy data

        URI astronomyUri = UriComponentsBuilder.fromUriString("https://api.sunrise-sunset.org/json").queryParam("lat", latitude)
            .queryParam("lng", longitude).queryParam("date", "today").build().toUri();

        JsonNode astroResponse = restTemplate.exchange(astronomyUri, HttpMethod.GET, requestEntity, JsonNode.class).getBody();
        JsonNode astroResults = astroResponse.get("results");
        dailyForecastsMap.put("Sun", Map.of("Rise", astroResults.get("sunrise"), "Set", astroResults.get("sunset")));

        JsonNode temp = objectMapper.convertValue(dailyForecastsMap, JsonNode.class);
        log.info("Daily Forecast Map: {}", temp.toPrettyString());

        weatherReport.put("Headline", Map.of("Text", firstPeriod.get("shortForecast")));
        weatherReport.put("DailyForecasts", List.of(dailyForecastsMap));

        resultMap.put("weatherReport", weatherReport);
        JsonNode temp2 = objectMapper.convertValue(resultMap, JsonNode.class);
        log.info("Result Map: {}", temp2.toPrettyString());
        return resultMap;
    }

    private String getCategory(JsonNode currAir, String pollutant) {
        String category = null;
        Integer value = currAir.get(pollutant).asInt();
        switch (pollutant) {
        case ("pm10"):
            if (value < 55)
                category = "Good";
            else if (value < 154)
                category = "Moderate";
            else
                category = "Unhealthy";
            break;
        case ("pm2_5"):
            if (value < 12)
                category = "Good";
            else if (value < 35)
                category = "Moderate";
            else
                category = "Unhealthy";
            break;
        case ("carbon_monoxide"):
            if (value < 5042)
                category = "Good";
            else if (value < 10771)
                category = "Moderate";
            else
                category = "Unhealthy";
            break;
        case ("ozone"):
            if (value < 55)
                category = "Good";
            else if (value < 70)
                category = "Moderate";
            else
                category = "Unhealthy";
            break;
        default:
            category = "Unknown";
        }

        return category;
    }

}
