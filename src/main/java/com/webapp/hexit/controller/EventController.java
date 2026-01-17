package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.repository.EventRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

  private final EventRepository eventRepository;

  public EventController(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @GetMapping("/all")
  public List<Event> getAllEvents() {
    return eventRepository.findAll();
  }

  @PostMapping("/add")
  public ResponseEntity<Event> addEvent(@RequestBody Event event) {
    if (
      event.getTitle() == null ||
      event.getDescription() == null ||
      event.getType() == null
    ) {
      return ResponseEntity.badRequest().build();
    }
    Event saved = eventRepository.save(event);
    return ResponseEntity.ok(saved);
  }
}
