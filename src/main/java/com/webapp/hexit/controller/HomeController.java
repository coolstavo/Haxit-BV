package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.repository.EventRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

  private final EventRepository eventRepository;

  public HomeController(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @GetMapping("/")
  public String home(
    @RequestParam(name = "titel", required = false) String titel,
    @RequestParam(name = "type", required = false) String type,
    Model model
  ) {
    List<Event> events = eventRepository.findAll();

    if (titel != null && !titel.isBlank()) {
      events = events
        .stream()
        .filter(event ->
          event.getTitle().toLowerCase().contains(titel.toLowerCase())
        )
        .collect(Collectors.toList());
    }

    if (type != null && !type.isBlank()) {
      events = events
        .stream()
        .filter(event ->
          event.getType().toLowerCase().contains(type.toLowerCase())
        )
        .collect(Collectors.toList());
    }

    model.addAttribute("events", events);
    model.addAttribute("username", "Gast");
    model.addAttribute("loginRequired", false);
    return "index";
  }

  @GetMapping("/admin")
  public String adminDashboard() {
    // Return the admin dashboard template
    return "admin";
  }

  @GetMapping("/{username}")
  public String homeWithUsername(
    @PathVariable String username,
    @RequestParam(
      name = "loginRequired",
      required = false
    ) Boolean loginRequired,
    Model model
  ) {
    List<Event> events = eventRepository.findAll();
    model.addAttribute("events", events);
    model.addAttribute(
      "username",
      (username != null && !username.isBlank()) ? username : "Gast"
    );
    model.addAttribute("loginRequired", loginRequired != null && loginRequired);
    return "index";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model, Exception ex) {
    model.addAttribute(
      "errorMessage",
      "Er is een fout opgetreden! Probeer het later opnieuw."
    );
    return "error";
  }
}
