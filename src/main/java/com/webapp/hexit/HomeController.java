package com.webapp.hexit;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.repository.EventRepository;
import java.util.List;
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
  public String home(Model model) {
    List<Event> events = eventRepository.findAll();
    model.addAttribute("events", events);
    model.addAttribute("username", "Gast");
    model.addAttribute("loginRequired", false);
    return "index";
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
