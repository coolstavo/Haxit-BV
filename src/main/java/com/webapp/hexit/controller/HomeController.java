package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.Lesson;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.repository.EventRepository;
import com.webapp.hexit.repository.InstrumentRepository;
import com.webapp.hexit.repository.LessonRepository;
import com.webapp.hexit.repository.UserRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

  private final EventRepository eventRepository;
  private final LessonRepository lessonRepository;
  private final UserRepository userRepository;
  private final InstrumentRepository instrumentRepository;

  public HomeController(
    EventRepository eventRepository,
    LessonRepository lessonRepository,
    UserRepository userRepository,
    InstrumentRepository instrumentRepository
  ) {
    this.eventRepository = eventRepository;
    this.lessonRepository = lessonRepository;
    this.userRepository = userRepository;
    this.instrumentRepository = instrumentRepository;
  }

  @GetMapping("/")
  public String home(
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "types", required = false) String types,
    @RequestParam(name = "instruments", required = false) String instruments,
    Model model
  ) {
    List<Event> events = eventRepository.findAll();
    List<Lesson> lessons = lessonRepository.findAll();

    // Parse types filter (comma-separated)
    final Set<String> selectedTypes = new HashSet<>();
    if (types != null && !types.isBlank()) {
      selectedTypes.addAll(Arrays.asList(types.split(",")));
    }

    // Parse instruments filter (comma-separated)
    final Set<String> selectedInstruments = new HashSet<>();
    if (instruments != null && !instruments.isBlank()) {
      selectedInstruments.addAll(Arrays.asList(instruments.split(",")));
    }

    // Filter by name search
    if (name != null && !name.isBlank()) {
      String searchTerm = name.toLowerCase();
      events = events
        .stream()
        .filter(event -> event.getTitle().toLowerCase().contains(searchTerm))
        .collect(Collectors.toList());

      lessons = lessons
        .stream()
        .filter(
          lesson ->
            (lesson.getInstrument() != null &&
              lesson
                .getInstrument()
                .getNaam()
                .toLowerCase()
                .contains(searchTerm)) ||
            (lesson.getDescription() != null &&
              lesson.getDescription().toLowerCase().contains(searchTerm)) ||
            (lesson.getDocent() != null &&
              lesson.getDocent().getNaam().toLowerCase().contains(searchTerm))
        )
        .collect(Collectors.toList());
    }

    // Filter by type (Lesson, Event, Jam)
    if (!selectedTypes.isEmpty()) {
      final boolean includeLesson = selectedTypes.contains("Lesson");
      final boolean includeEvent = selectedTypes.contains("Event");
      final boolean includeJam = selectedTypes.contains("Jam");

      events = events
        .stream()
        .filter(event -> {
          String eventType = event.getType().toLowerCase();
          return (
            (includeEvent && eventType.contains("event")) ||
            (includeJam && eventType.contains("jam"))
          );
        })
        .collect(Collectors.toList());

      if (!includeLesson) {
        lessons = List.of();
      }
    }

    // Filter by instruments
    if (!selectedInstruments.isEmpty()) {
      lessons = lessons
        .stream()
        .filter(
          lesson ->
            lesson.getInstrument() != null &&
            selectedInstruments.contains(lesson.getInstrument().getNaam())
        )
        .collect(Collectors.toList());
    }

    model.addAttribute("events", events);
    model.addAttribute("lessons", lessons);
    model.addAttribute("instruments", instrumentRepository.findAll());
    model.addAttribute("username", "Gast");
    model.addAttribute("userRole", "GAST");
    model.addAttribute("loginRequired", false);
    return "index";
  }

  @GetMapping("/admin")
  public String adminDashboard() {
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
    List<Lesson> lessons = lessonRepository.findAll();

    model.addAttribute("events", events);
    model.addAttribute("lessons", lessons);
    model.addAttribute("instruments", instrumentRepository.findAll());
    model.addAttribute(
      "username",
      (username != null && !username.isBlank()) ? username : "Gast"
    );

    // Bepaal userRole op basis van User role
    String userRole = "MUZIKANT"; // default
    var user = userRepository.findByUsername(username);
    if (user.isPresent()) {
      if (user.get().getRole() == Role.BEDRIJF) {
        userRole = "BEDRIJF";
      } else if (user.get().getRole() == Role.DOCENT) {
        userRole = "DOCENT";
      } else {
        userRole = "MUZIKANT";
      }
    }

    model.addAttribute("userRole", userRole);
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
