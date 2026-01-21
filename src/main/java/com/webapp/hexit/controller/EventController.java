package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.EventRepository;
import com.webapp.hexit.repository.UserRepository;
import com.webapp.hexit.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(
        @RequestBody Event event,
        @RequestParam(required = false) String username
    ) {
        System.out.println("USERNAME = " + username);
        System.out.println("EVENT TITLE = " + event.getTitle());
        if (
            event.getTitle() == null ||
            event.getDescription() == null ||
            event.getType() == null
        ) {
            return ResponseEntity.badRequest().build();
        }

        // Controleer of de username is meegegeven
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Of gebruik een standaardwaarde
        }

        // Zoek de gebruiker op basis van de gebruikersnaam
        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Koppel de gebruiker aan het event
        event.setUser(user);

        // Sla het event op
        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(saved);
    }
}
