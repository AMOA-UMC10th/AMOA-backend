package com.amoa.server.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/health")
    @Operation(security = {})
    public String healthCheck() {
        return "OK";
    }
}