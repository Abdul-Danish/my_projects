package com.weather.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weather.service.WeatherService;

@RestController
@RequestMapping("/api/v1")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getForcast(@RequestParam String city,
        @RequestParam String state, @RequestParam String country) {
        return ResponseEntity.ok(weatherService.getForecast(city, state, country));
    }
    
}
