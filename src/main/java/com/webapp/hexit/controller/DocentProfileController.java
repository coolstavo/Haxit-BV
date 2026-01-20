package com.webapp.hexit.controller;

import com.webapp.hexit.model.Docent;
import com.webapp.hexit.model.Instrument;
import com.webapp.hexit.model.Lesson;
import com.webapp.hexit.model.LessonBooking;
import com.webapp.hexit.model.LessonStatus;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.DocentRepository;
import com.webapp.hexit.repository.InstrumentRepository;
import com.webapp.hexit.repository.LessonBookingRepository;
import com.webapp.hexit.repository.LessonRepository;
import com.webapp.hexit.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class DocentProfileController {

  private final DocentRepository docentRepository;
  private final LessonRepository lessonRepository;
  private final InstrumentRepository instrumentRepository;
  private final LessonBookingRepository lessonBookingRepository;
  private final UserRepository userRepository;

  public DocentProfileController(
    DocentRepository docentRepository,
    LessonRepository lessonRepository,
    InstrumentRepository instrumentRepository,
    LessonBookingRepository lessonBookingRepository,
    UserRepository userRepository
  ) {
    this.docentRepository = docentRepository;
    this.lessonRepository = lessonRepository;
    this.instrumentRepository = instrumentRepository;
    this.lessonBookingRepository = lessonBookingRepository;
    this.userRepository = userRepository;
  }

  /**
   * Helper method to calculate lesson statistics for a given lesson
   * @param lessonId the ID of the lesson
   * @return a map with "planned" and "pending" counts
   */
  private Map<String, Long> getLessonStatistics(Long lessonId) {
    Map<String, Long> stats = new HashMap<>();

    List<LessonBooking> bookings = lessonBookingRepository.findByLessonId(
      lessonId
    );

    long plannedCount = bookings
      .stream()
      .filter(b -> b.getStatus() == LessonStatus.GEACCEPTEERD)
      .count();

    long pendingCount = bookings
      .stream()
      .filter(b -> b.getStatus() == LessonStatus.AANGEVRAAGD)
      .count();

    stats.put("planned", plannedCount);
    stats.put("pending", pendingCount);

    return stats;
  }

  /**
   * Helper method to prepare lesson statistics for all lessons
   * @param lessons the list of lessons
   * @return a map of lessonId -> statistics map
   */
  private Map<Long, Map<String, Long>> getAllLessonStatistics(
    List<Lesson> lessons
  ) {
    Map<Long, Map<String, Long>> allStats = new HashMap<>();

    for (Lesson lesson : lessons) {
      allStats.put(lesson.getId(), getLessonStatistics(lesson.getId()));
    }

    return allStats;
  }

  /**
   * Public method for unified profile routing
   */
  public String getDocentProfile(String username, Model model) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      return handleError(model);
    }

    Optional<Docent> docent = docentRepository.findByUser(user);

    if (docent.isPresent()) {
      List<Lesson> lessons = lessonRepository.findByDocentId(
        docent.get().getId()
      );
      model.addAttribute("docent", docent.get());
      model.addAttribute("lessons", lessons);
      model.addAttribute("lessonStats", getAllLessonStatistics(lessons));
    } else {
      model.addAttribute("lessons", List.of());
      model.addAttribute("lessonStats", new HashMap<>());
    }

    model.addAttribute("docentName", username);
    model.addAttribute("username", username);
    model.addAttribute("userRole", "DOCENT");
    model.addAttribute("edit", false);
    return "docent-profile";
  }

  /**
   * Public method for unified edit routing
   */
  public String editDocentProfile(String username, Model model) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      return handleError(model);
    }

    Optional<Docent> docent = docentRepository.findByUser(user);

    if (docent.isPresent()) {
      List<Lesson> lessons = lessonRepository.findByDocentId(
        docent.get().getId()
      );
      model.addAttribute("docent", docent.get());
      model.addAttribute("lessons", lessons);
      model.addAttribute("lessonStats", getAllLessonStatistics(lessons));
    } else {
      model.addAttribute("lessons", List.of());
      model.addAttribute("lessonStats", new HashMap<>());
    }

    model.addAttribute("docentName", username);
    model.addAttribute("username", username);
    model.addAttribute("userRole", "DOCENT");
    model.addAttribute("edit", true);

    // Fetch instruments from database
    List<Instrument> instruments = instrumentRepository.findAll();
    model.addAttribute("instruments", instruments);

    model.addAttribute(
      "levels",
      new String[] { "Beginner", "Intermediate", "Advanced" }
    );
    model.addAttribute(
      "lessonForms",
      new String[] {
        "Fysiek op locatie docent",
        "Fysiek bij student thuis",
        "Online/Video-call",
      }
    );
    model.addAttribute(
      "rateTypes",
      new String[] { "per 30 minuten", "per uur" }
    );
    return "docent-profile";
  }

  /**
   * Original method for backwards compatibility (by naam)
   */
  @GetMapping("/profile/docent/{docentnaam}/edit")
  public String editDocentProfileByNaam(
    @PathVariable String docentnaam,
    Model model
  ) {
    User user = userRepository.findByUsername(docentnaam).orElse(null);

    if (user == null) {
      return handleError(model);
    }

    Optional<Docent> docent = docentRepository.findByUser(user);

    if (docent.isPresent()) {
      List<Lesson> lessons = lessonRepository.findByDocentId(
        docent.get().getId()
      );
      model.addAttribute("docent", docent.get());
      model.addAttribute("lessons", lessons);
      model.addAttribute("lessonStats", getAllLessonStatistics(lessons));
    } else {
      model.addAttribute("lessons", List.of());
      model.addAttribute("lessonStats", new HashMap<>());
    }

    model.addAttribute("docentName", docentnaam);
    model.addAttribute("username", docentnaam);
    model.addAttribute("userRole", "DOCENT");
    model.addAttribute("edit", true);

    // Fetch instruments from database
    List<Instrument> instruments = instrumentRepository.findAll();
    model.addAttribute("instruments", instruments);

    model.addAttribute(
      "levels",
      new String[] { "Beginner", "Intermediate", "Advanced" }
    );
    model.addAttribute(
      "lessonForms",
      new String[] {
        "Fysiek op locatie docent",
        "Fysiek bij student thuis",
        "Online/Video-call",
      }
    );
    model.addAttribute(
      "rateTypes",
      new String[] { "per 30 minuten", "per uur" }
    );
    return "docent-profile";
  }

  @PostMapping("/profile/docent/save-profile")
  public String saveDocentProfile(
    @RequestParam String naam,
    @RequestParam(required = false) String specialisatie,
    @RequestParam(required = false) String biografie,
    @RequestParam(required = false) Integer ervaringsjaren,
    @RequestParam(required = false) String kwalificaties,
    Model model
  ) {
    try {
      User user = userRepository.findByUsername(naam).orElse(null);

      if (user == null) {
        model.addAttribute("errorMessage", "Gebruiker niet gevonden");
        return "error";
      }

      Optional<Docent> docent = docentRepository.findByUser(user);

      if (docent.isEmpty()) {
        model.addAttribute("errorMessage", "Docent niet gevonden");
        return "error";
      }

      Docent d = docent.get();
      d.setSpecialisatie(specialisatie);
      d.setBiografie(biografie);
      d.setErvaringsjaren(ervaringsjaren);
      d.setKwalificaties(kwalificaties);

      docentRepository.save(d);
      return "redirect:/profile/" + naam;
    } catch (Exception e) {
      model.addAttribute("errorMessage", "Fout bij opslaan van profiel");
      return "error";
    }
  }

  @PostMapping("/profile/docent/save-lesson")
  public String saveLessonOffering(
    @RequestParam String docentName,
    @RequestParam Long instrumentId,
    @RequestParam(required = false) String[] levels,
    @RequestParam String lessonForm,
    @RequestParam Double rate,
    @RequestParam String rateType,
    @RequestParam(required = false) String description,
    @RequestParam(required = false) String location,
    @RequestParam(required = false) Double lat,
    @RequestParam(required = false) Double lng,
    Model model
  ) {
    try {
      User user = userRepository.findByUsername(docentName).orElse(null);

      if (user == null) {
        model.addAttribute("errorMessage", "Gebruiker niet gevonden");
        return "error";
      }

      Optional<Docent> docent = docentRepository.findByUser(user);

      if (docent.isEmpty()) {
        model.addAttribute("errorMessage", "Docent niet gevonden");
        return "error";
      }

      Optional<Instrument> instrument = instrumentRepository.findById(
        instrumentId
      );
      if (instrument.isEmpty()) {
        model.addAttribute("errorMessage", "Instrument niet gevonden");
        return "error";
      }

      Lesson lesson = new Lesson(docent.get());
      lesson.setInstrument(instrument.get());
      lesson.setLessonForm(lessonForm);
      lesson.setRate(rate);
      lesson.setRateType(rateType);
      lesson.setDescription(description);
      lesson.setLocation(location);
      lesson.setLat(lat);
      lesson.setLng(lng);

      if (levels != null && levels.length > 0) {
        lesson.setLevels(String.join(", ", levels));
      } else {
        lesson.setLevels("");
      }

      lessonRepository.save(lesson);
      return "redirect:/profile/" + docentName + "/edit";
    } catch (Exception e) {
      model.addAttribute("errorMessage", "Fout bij opslaan van les");
      return "error";
    }
  }

  @GetMapping("/profile/docent/delete-lesson/{id}")
  public String deleteLesson(
    @PathVariable Long id,
    @RequestParam String docentName
  ) {
    lessonRepository.deleteById(id);
    return "redirect:/profile/" + docentName + "/edit";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model) {
    model.addAttribute("errorMessage", "Er is een fout opgetreden!");
    return "error";
  }
}
