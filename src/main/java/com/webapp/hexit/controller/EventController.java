package com.webapp.hexit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    // locale storage hier voor nu, later in database!
    private static List<Map<String, Object>> events = new ArrayList<>();

    public static List<Map<String, Object>> getAllEvents() {
        return new ArrayList<>(events);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addEvent(@RequestBody Map<String, Object> eventData) {
        
        if (!eventData.containsKey("title") || !eventData.containsKey("description") ||
            !eventData.containsKey("lat") || !eventData.containsKey("lng") || !eventData.containsKey("type")) {
            return ResponseEntity.badRequest().build();
        }

        // event toevoegen aan locale storage
        events.add(eventData);

        return ResponseEntity.ok(eventData);
    }
}
