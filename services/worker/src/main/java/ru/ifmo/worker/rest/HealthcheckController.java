package ru.ifmo.worker.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthcheckController {
    @GetMapping("/ping")
    String ping() {
        return "OK!";
    }
}