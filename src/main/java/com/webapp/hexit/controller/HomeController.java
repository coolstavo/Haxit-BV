package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.Lesson;
import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.EventLikeRepository;
import com.webapp.hexit.repository.EventRepository;
import com.webapp.hexit.repository.GenreRepository;
import com.webapp.hexit.repository.InstrumentRepository;
import com.webapp.hexit.repository.JamLikeRepository;
import com.webapp.hexit.repository.JamRepository;
import com.webapp.hexit.repository.LessonLikeRepository;
import com.webapp.hexit.repository.LessonRepository;
import com.webapp.hexit.repository.MuzikantRepository;
import com.webapp.hexit.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

  private final EventRepository eventRepository;
  private final LessonRepository lessonRepository;
  private final JamRepository jamRepository;
  private final UserRepository userRepository;
  private final InstrumentRepository instrumentRepository;
  private final GenreRepository genreRepository;
  private final EventLikeRepository eventLikeRepository;
  private final JamLikeRepository jamLikeRepository;
  private final LessonLikeRepository lessonLikeRepository;
  private final MuzikantRepository muzikantRepository;

  public HomeController(
    EventRepository eventRepository,
    LessonRepository lessonRepository,
    JamRepository jamRepository,
    UserRepository userRepository,
    InstrumentRepository instrumentRepository,
    GenreRepository genreRepository,
    EventLikeRepository eventLikeRepository,
    JamLikeRepository jamLikeRepository,
    LessonLikeRepository lessonLikeRepository,
    MuzikantRepository muzikantRepository
  ) {
    this.eventRepository = eventRepository;
    this.lessonRepository = lessonRepository;
    this.jamRepository = jamRepository;
    this.userRepository = userRepository;
    this.instrumentRepository = instrumentRepository;
    this.genreRepository = genreRepository;
    this.eventLikeRepository = eventLikeRepository;
    this.jamLikeRepository = jamLikeRepository;
    this.lessonLikeRepository = lessonLikeRepository;
    this.muzikantRepository = muzikantRepository;
  }

  @GetMapping("/")
  public String home(
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "types", required = false) String types,
    @RequestParam(name = "instruments", required = false) String instruments,
    @RequestParam(name = "genres", required = false) String genres,
    @RequestParam(name = "likedByMe", required = false) Boolean likedByMe,
    Model model
  ) {
    List<Event> events = eventRepository.findAll();
    List<Lesson> lessons = lessonRepository.findAll();
    List<Jam> jams = jamRepository.findAll();

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

    // Parse genres filter (comma-separated)
    final Set<String> selectedGenres = new HashSet<>();
    if (genres != null && !genres.isBlank()) {
      selectedGenres.addAll(Arrays.asList(genres.split(",")));
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

      jams = jams
        .stream()
        .filter(
          jam ->
            (jam.getTitle() != null &&
              jam.getTitle().toLowerCase().contains(searchTerm)) ||
            (jam.getDescription() != null &&
              jam.getDescription().toLowerCase().contains(searchTerm))
        )
        .collect(Collectors.toList());
    }

    // Filter by type (Lesson, Event, Jam)
    if (!selectedTypes.isEmpty()) {
      final boolean includeLesson = selectedTypes.contains("Lesson");
      final boolean includeEvent = selectedTypes.contains("Event");
      final boolean includeJam = selectedTypes.contains("Jam");

      if (!includeEvent) {
        events = List.of();
      }

      if (!includeLesson) {
        lessons = List.of();
      }

      if (!includeJam) {
        jams = List.of();
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

      events = events
        .stream()
        .filter(event ->
          event
            .getInstruments()
            .stream()
            .anyMatch(instrument ->
              selectedInstruments.contains(instrument.getNaam())
            )
        )
        .collect(Collectors.toList());

      jams = jams
        .stream()
        .filter(jam ->
          jam
            .getInstruments()
            .stream()
            .anyMatch(instrument ->
              selectedInstruments.contains(instrument.getNaam())
            )
        )
        .collect(Collectors.toList());
    }

    // Filter by genres
    if (!selectedGenres.isEmpty()) {
      events = events
        .stream()
        .filter(event ->
          event
            .getGenres()
            .stream()
            .anyMatch(genre -> selectedGenres.contains(genre.getName()))
        )
        .collect(Collectors.toList());

      lessons = lessons
        .stream()
        .filter(lesson ->
          lesson
            .getGenres()
            .stream()
            .anyMatch(genre -> selectedGenres.contains(genre.getName()))
        )
        .collect(Collectors.toList());

      jams = jams
        .stream()
        .filter(jam ->
          jam
            .getGenres()
            .stream()
            .anyMatch(genre -> selectedGenres.contains(genre.getName()))
        )
        .collect(Collectors.toList());
    }

    // Create like count maps
    Map<Long, Long> eventLikeCounts = new HashMap<>();
    Map<Long, Long> jamLikeCounts = new HashMap<>();
    Map<Long, Long> lessonLikeCounts = new HashMap<>();

    for (Event event : events) {
      eventLikeCounts.put(
        event.getId(),
        eventLikeRepository.countByEventId(event.getId())
      );
    }

    for (Jam jam : jams) {
      jamLikeCounts.put(
        jam.getId(),
        jamLikeRepository.countByJamId(jam.getId())
      );
    }

    for (Lesson lesson : lessons) {
      lessonLikeCounts.put(
        lesson.getId(),
        lessonLikeRepository.countByLessonId(lesson.getId())
      );
    }

    // Empty like sets for guest users
    model.addAttribute("events", events);
    model.addAttribute("lessons", lessons);
    model.addAttribute("jams", jams);
    model.addAttribute("eventLikeCounts", eventLikeCounts);
    model.addAttribute("jamLikeCounts", jamLikeCounts);
    model.addAttribute("lessonLikeCounts", lessonLikeCounts);
    model.addAttribute("eventUserLikes", new HashSet<Long>());
    model.addAttribute("jamUserLikes", new HashSet<Long>());
    model.addAttribute("lessonUserLikes", new HashSet<Long>());
    model.addAttribute("instruments", instrumentRepository.findAll());
    model.addAttribute("genres", genreRepository.findAll());
    model.addAttribute("username", "Gast");
    model.addAttribute("userRole", "GAST");
    model.addAttribute("loginRequired", false);
    model.addAttribute("aanbevelingen", List.of());
    return "index";
  }

  @GetMapping("/admin")
  public String adminDashboard() {
    return "admin";
  }

  @GetMapping("/home")
  public String homeWithSession(
    @RequestParam(
      name = "loginRequired",
      required = false
    ) Boolean loginRequired,
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "types", required = false) String types,
    @RequestParam(name = "instruments", required = false) String instruments,
    @RequestParam(name = "genres", required = false) String genres,
    @RequestParam(name = "likedByMe", required = false) Boolean likedByMe,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/?loginRequired=true";
    }
    String username = usernameObj.toString();

    List<Muzikant> aanbevelingen = new ArrayList<>();
    
    User currentUser = userRepository.findByUsername(username).orElse(null);
    
    if (currentUser != null) {
        Muzikant ik = muzikantRepository.findByUser(currentUser).orElse(null);

        if (ik != null && !ik.getGenres().isEmpty()) {

            // Haal muzikanten op met gedeelde genres
            List<Muzikant> muzikanten = muzikantRepository.findMatches(
                ik.getGenres(), 
                username
            );

            // Sorteer muzikanten eerst op stad, daarna aantal genres
            aanbevelingen = muzikanten.stream()
                .sorted((m1, m2) -> {
                    // Eerst checken of ze in dezelfde stad wonen
                    boolean m1InStad = ik.getStad() != null && ik.getStad().equals(m1.getStad());
                    boolean m2InStad = ik.getStad() != null && ik.getStad().equals(m2.getStad());

                    // Als de een wel in zelfde stad woont en de ander niet komt die eerst
                    if (m1InStad != m2InStad) {
                        return m1InStad ? -1 : 1; 
                    }

                    // Als ze allebei wel of niet in zelfde stad wonen sorteer op aantal gedeelde genres
                    long count1 = m1.getGenres().stream().filter(ik.getGenres()::contains).count();
                    long count2 = m2.getGenres().stream().filter(ik.getGenres()::contains).count();

                    // Vergelijk aantallen in aflopende volgorde
                    return Long.compare(count2, count1);
                })
                .collect(Collectors.toList());
        }
    }

    List<Event> events = eventRepository.findAll();
    List<Lesson> lessons = lessonRepository.findAll();
    List<Jam> jams = jamRepository.findAll();

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

    // Parse genres filter (comma-separated)
    final Set<String> selectedGenres = new HashSet<>();
    if (genres != null && !genres.isBlank()) {
      selectedGenres.addAll(Arrays.asList(genres.split(",")));
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

      jams = jams
        .stream()
        .filter(
          jam ->
            (jam.getTitle() != null &&
              jam.getTitle().toLowerCase().contains(searchTerm)) ||
            (jam.getDescription() != null &&
              jam.getDescription().toLowerCase().contains(searchTerm))
        )
        .collect(Collectors.toList());
    }

    // Filter by type (Lesson, Event, Jam)
    if (!selectedTypes.isEmpty()) {
      final boolean includeLesson = selectedTypes.contains("Lesson");
      final boolean includeEvent = selectedTypes.contains("Event");
      final boolean includeJam = selectedTypes.contains("Jam");

      if (!includeEvent) {
        events = List.of();
      }

      if (!includeLesson) {
        lessons = List.of();
      }

      if (!includeJam) {
        jams = List.of();
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

      events = events
        .stream()
        .filter(event ->
          event
            .getInstruments()
            .stream()
            .anyMatch(instrument ->
              selectedInstruments.contains(instrument.getNaam())
            )
        )
        .collect(Collectors.toList());

      jams = jams
        .stream()
        .filter(jam ->
          jam
            .getInstruments()
            .stream()
            .anyMatch(instrument ->
              selectedInstruments.contains(instrument.getNaam())
            )
        )
        .collect(Collectors.toList());
    }

    // Filter by genres
    if (!selectedGenres.isEmpty()) {
      events = events
        .stream()
        .filter(event ->
          event
            .getGenres()
            .stream()
            .anyMatch(genre -> selectedGenres.contains(genre.getName()))
        )
        .collect(Collectors.toList());

      lessons = lessons
        .stream()
        .filter(lesson ->
          lesson
            .getGenres()
            .stream()
            .anyMatch(genre -> selectedGenres.contains(genre.getName()))
        )
        .collect(Collectors.toList());

      jams = jams
        .stream()
        .filter(jam ->
          jam
            .getGenres()
            .stream()
            .anyMatch(genre -> selectedGenres.contains(genre.getName()))
        )
        .collect(Collectors.toList());
    }

    // Get current user
    var userOpt = userRepository.findByUsername(username);

    // Filter by liked by me
    if (likedByMe != null && likedByMe && userOpt.isPresent()) {
      final var user = userOpt.get();

      events = events
        .stream()
        .filter(event -> eventLikeRepository.existsByUserAndEvent(user, event))
        .collect(Collectors.toList());

      lessons = lessons
        .stream()
        .filter(lesson ->
          lessonLikeRepository.existsByUserAndLesson(user, lesson)
        )
        .collect(Collectors.toList());

      jams = jams
        .stream()
        .filter(jam -> jamLikeRepository.existsByUserAndJam(user, jam))
        .collect(Collectors.toList());
    }

    // Create like count maps
    Map<Long, Long> eventLikeCounts = new HashMap<>();
    Map<Long, Long> jamLikeCounts = new HashMap<>();
    Map<Long, Long> lessonLikeCounts = new HashMap<>();

    // Create sets to track which items the current user has liked
    Set<Long> eventUserLikes = new HashSet<>();
    Set<Long> jamUserLikes = new HashSet<>();
    Set<Long> lessonUserLikes = new HashSet<>();

    for (Event event : events) {
      eventLikeCounts.put(
        event.getId(),
        eventLikeRepository.countByEventId(event.getId())
      );

      // Check if current user has liked this event
      if (
        userOpt.isPresent() &&
        eventLikeRepository.existsByUserAndEvent(userOpt.get(), event)
      ) {
        eventUserLikes.add(event.getId());
      }
    }

    for (Jam jam : jams) {
      jamLikeCounts.put(
        jam.getId(),
        jamLikeRepository.countByJamId(jam.getId())
      );

      // Check if current user has liked this jam
      if (
        userOpt.isPresent() &&
        jamLikeRepository.existsByUserAndJam(userOpt.get(), jam)
      ) {
        jamUserLikes.add(jam.getId());
      }
    }

    for (Lesson lesson : lessons) {
      lessonLikeCounts.put(
        lesson.getId(),
        lessonLikeRepository.countByLessonId(lesson.getId())
      );

      // Check if current user has liked this lesson
      if (
        userOpt.isPresent() &&
        lessonLikeRepository.existsByUserAndLesson(userOpt.get(), lesson)
      ) {
        lessonUserLikes.add(lesson.getId());
      }
    }

    model.addAttribute("events", events);
    model.addAttribute("lessons", lessons);
    model.addAttribute("jams", jams);
    model.addAttribute("aanbevelingen", aanbevelingen);
    model.addAttribute("eventLikeCounts", eventLikeCounts);
    model.addAttribute("jamLikeCounts", jamLikeCounts);
    model.addAttribute("lessonLikeCounts", lessonLikeCounts);
    model.addAttribute("eventUserLikes", eventUserLikes);
    model.addAttribute("jamUserLikes", jamUserLikes);
    model.addAttribute("lessonUserLikes", lessonUserLikes);
    model.addAttribute("instruments", instrumentRepository.findAll());
    model.addAttribute("genres", genreRepository.findAll());
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
