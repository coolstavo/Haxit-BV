package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.EventRepository;
import com.webapp.hexit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/event")
public class EventController {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;

  public EventController(
    EventRepository eventRepository,
    UserRepository userRepository
  ) {
    this.eventRepository = eventRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/{eventId}/{username}")
  public String getEventPage(
    @PathVariable Long eventId,
    @PathVariable String username,
    Model model
  ) {
    Optional<Event> eventOpt = eventRepository.findById(eventId);
    if (eventOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Event niet gevonden");
      return "error";
    }

    Event event = eventOpt.get();
    model.addAttribute("event", event);

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();
    model.addAttribute("user", user);
    model.addAttribute("username", username);

    // Check if user is the event owner (company)
    boolean isOwner =
      event.getCompanyUser() != null &&
      event.getCompanyUser().getId().equals(user.getId());
    model.addAttribute("isOwner", isOwner);

    return "event";
  }

  @PostMapping("/{eventId}/{username}/delete")
  public String deleteEvent(
    @PathVariable Long eventId,
    @PathVariable String username,
    Model model
  ) {
    Optional<Event> eventOpt = eventRepository.findById(eventId);
    if (eventOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Event niet gevonden");
      return "error";
    }

    Event event = eventOpt.get();

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    // Verify user is the owner
    if (
      event.getCompanyUser() == null ||
      !event.getCompanyUser().getId().equals(user.getId())
    ) {
      model.addAttribute(
        "errorMessage",
        "Geen toegang om dit event te verwijderen"
      );
      return "error";
    }

    eventRepository.delete(event);
    return "redirect:/profile/" + username;
  }
}

@RestController
@RequestMapping("/api/events")
class EventApiController {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;

  EventApiController(
    EventRepository eventRepository,
    UserRepository userRepository
  ) {
    this.eventRepository = eventRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/all")
  public List<Event> getAllEvents() {
    return eventRepository.findAll();
  }

  @PostMapping("/add")
  public ResponseEntity<Event> addEvent(@RequestBody Event event) {
    if (event.getTitle() == null || event.getDescription() == null) {
      return ResponseEntity.badRequest().build();
    }

    if (
      event.getCompanyUser() == null || event.getCompanyUser().getId() == null
    ) {
      return ResponseEntity.badRequest().build();
    }

    Event saved = eventRepository.save(event);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/company/{companyUserId}")
  public List<Event> getCompanyEvents(@PathVariable Long companyUserId) {
    return eventRepository.findByCompanyUserId(companyUserId);
  }

  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
    Optional<Event> eventOpt = eventRepository.findById(eventId);
    if (eventOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    eventRepository.delete(eventOpt.get());
    return ResponseEntity.ok().build();
  }
}
