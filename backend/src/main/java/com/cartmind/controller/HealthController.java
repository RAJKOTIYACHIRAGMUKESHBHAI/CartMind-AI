package com.cartmind.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "CartMind AI Backend is running";
    }

    @GetMapping("/actuator/health")
    public Map<String, String> actuatorHealth() {
        return Map.of("status", "UP");
    }
}