package com.webapp.hexit.controller;

import com.webapp.hexit.model.*;
import com.webapp.hexit.repository.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LessonController {

  private final LessonRepository lessonRepository;
  private final LessonBookingRepository lessonBookingRepository;
  private final UserRepository userRepository;
  private final MuzikantRepository muzikantRepository;
  private final DocentRepository docentRepository;

  public LessonController(
    LessonRepository lessonRepository,
    LessonBookingRepository lessonBookingRepository,
    UserRepository userRepository,
    MuzikantRepository muzikantRepository,
    DocentRepository docentRepository
  ) {
    this.lessonRepository = lessonRepository;
    this.lessonBookingRepository = lessonBookingRepository;
    this.userRepository = userRepository;
    this.muzikantRepository = muzikantRepository;
    this.docentRepository = docentRepository;
  }

  @GetMapping("/lesson/{lessonId}/{username}")
  public String getLessonPage(
    @PathVariable Long lessonId,
    @PathVariable String username,
    Model model
  ) {
    Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
    if (lessonOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Les niet gevonden");
      return "error";
    }

    Lesson lesson = lessonOpt.get();
    model.addAttribute("lesson", lesson);

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();
    model.addAttribute("user", user);
    model.addAttribute("username", username);

    boolean isTeacher = user.getRole() == Role.DOCENT;
    boolean isStudent = user.getRole() == Role.MUZIKANT;

    model.addAttribute("isTeacher", isTeacher);
    model.addAttribute("isStudent", isStudent);

    List<LessonBooking> bookings;

    if (isTeacher) {
      bookings = lessonBookingRepository.findByLessonId(lessonId);
    } else if (isStudent) {
      Optional<Muzikant> muzikantOpt = muzikantRepository.findByUser(user);
      if (muzikantOpt.isPresent()) {
        bookings = lessonBookingRepository.findByLessonIdAndStudent(
          lessonId,
          muzikantOpt.get()
        );
      } else {
        bookings = List.of();
      }
    } else {
      bookings = lessonBookingRepository.findByLessonId(lessonId);
    }

    model.addAttribute("bookings", bookings);
    model.addAttribute("hasBookings", !bookings.isEmpty());

    return "lesson";
  }

  @PostMapping("/lesson/{lessonId}/{username}/request")
  public String requestLesson(
    @PathVariable Long lessonId,
    @PathVariable String username,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime proposal1,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime proposal2,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime proposal3,
    @RequestParam(required = false) String message,
    Model model
  ) {
    Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
    if (lessonOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Les niet gevonden");
      return "error";
    }

    Lesson lesson = lessonOpt.get();

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    if (user.getRole() != Role.MUZIKANT) {
      model.addAttribute(
        "errorMessage",
        "Alleen studenten kunnen lessen aanvragen"
      );
      return "error";
    }

    Optional<Muzikant> muzikantOpt = muzikantRepository.findByUser(user);
    if (muzikantOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Student profiel niet gevonden");
      return "error";
    }

    Muzikant student = muzikantOpt.get();

    LessonBooking booking = new LessonBooking();
    booking.setStudent(student);
    booking.setLesson(lesson);
    booking.setStatus(LessonStatus.AANGEVRAAGD);
    booking.setAcceptedByStudent(true);
    booking.setAcceptedByDocent(false);

    List<LocalDateTime> proposals = new ArrayList<>();
    if (proposal1 != null) proposals.add(proposal1);
    if (proposal2 != null) proposals.add(proposal2);
    if (proposal3 != null) proposals.add(proposal3);
    booking.setLessonProposals(proposals);

    lessonBookingRepository.save(booking);

    return "redirect:/lesson/" + lessonId + "/" + username + "?success=true";
  }

  @PostMapping("/lesson/{lessonId}/{username}/accept")
  public String acceptBooking(
    @PathVariable Long lessonId,
    @PathVariable String username,
    @RequestParam Long bookingId,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime chosenTime,
    Model model
  ) {
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    if (user.getRole() != Role.DOCENT) {
      model.addAttribute("errorMessage", "Alleen docenten kunnen accepteren");
      return "error";
    }

    Optional<LessonBooking> bookingOpt = lessonBookingRepository.findById(
      bookingId
    );
    if (bookingOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Boeking niet gevonden");
      return "error";
    }

    LessonBooking booking = bookingOpt.get();

    // Verify this teacher owns the lesson
    if (
      !booking.getLesson().getDocent().getUser().getId().equals(user.getId())
    ) {
      model.addAttribute("errorMessage", "Geen toegang tot deze boeking");
      return "error";
    }

    booking.setAcceptedByDocent(true);
    booking.setConfirmedTime(chosenTime);

    // If student already accepted, mark as fully accepted
    if (booking.isAcceptedByStudent()) {
      booking.setStatus(LessonStatus.GEACCEPTEERD);
    }

    lessonBookingRepository.save(booking);

    return "redirect:/lesson/" + lessonId + "/" + username;
  }

  @PostMapping("/lesson/{lessonId}/{username}/reject")
  public String rejectBooking(
    @PathVariable Long lessonId,
    @PathVariable String username,
    @RequestParam Long bookingId,
    Model model
  ) {
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    if (user.getRole() != Role.DOCENT) {
      model.addAttribute("errorMessage", "Alleen docenten kunnen afwijzen");
      return "error";
    }

    Optional<LessonBooking> bookingOpt = lessonBookingRepository.findById(
      bookingId
    );
    if (bookingOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Boeking niet gevonden");
      return "error";
    }

    LessonBooking booking = bookingOpt.get();

    // Verify this teacher owns the lesson
    if (
      !booking.getLesson().getDocent().getUser().getId().equals(user.getId())
    ) {
      model.addAttribute("errorMessage", "Geen toegang tot deze boeking");
      return "error";
    }

    booking.setStatus(LessonStatus.AFGEWEZEN);
    booking.setAcceptedByDocent(false);

    lessonBookingRepository.save(booking);

    return "redirect:/lesson/" + lessonId + "/" + username;
  }

  @PostMapping("/lesson/{lessonId}/{username}/update-proposals")
  public String updateProposals(
    @PathVariable Long lessonId,
    @PathVariable String username,
    @RequestParam Long bookingId,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime proposal1,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime proposal2,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime proposal3,
    Model model
  ) {
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    if (user.getRole() != Role.MUZIKANT) {
      model.addAttribute(
        "errorMessage",
        "Alleen studenten kunnen voorstellen wijzigen"
      );
      return "error";
    }

    Optional<LessonBooking> bookingOpt = lessonBookingRepository.findById(
      bookingId
    );
    if (bookingOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Boeking niet gevonden");
      return "error";
    }

    LessonBooking booking = bookingOpt.get();

    // Verify this student owns the booking
    Optional<Muzikant> muzikantOpt = muzikantRepository.findByUser(user);
    if (
      muzikantOpt.isEmpty() ||
      !booking.getStudent().getId().equals(muzikantOpt.get().getId())
    ) {
      model.addAttribute("errorMessage", "Geen toegang tot deze boeking");
      return "error";
    }

    // Only allow updating if rejected
    if (booking.getStatus() != LessonStatus.AFGEWEZEN) {
      model.addAttribute(
        "errorMessage",
        "Je kunt alleen afgewezen aanvragen opnieuw indienen"
      );
      return "error";
    }

    // Update proposals
    List<LocalDateTime> proposals = new ArrayList<>();
    if (proposal1 != null) proposals.add(proposal1);
    if (proposal2 != null) proposals.add(proposal2);
    if (proposal3 != null) proposals.add(proposal3);

    if (proposals.isEmpty()) {
      model.addAttribute(
        "errorMessage",
        "Je moet minimaal één voorstel indienen"
      );
      return "error";
    }

    booking.setLessonProposals(proposals);
    booking.setStatus(LessonStatus.AANGEVRAAGD);
    booking.setAcceptedByStudent(true);
    booking.setAcceptedByDocent(false);
    booking.setConfirmedTime(null);

    lessonBookingRepository.save(booking);

    return (
      "redirect:/lesson/" + lessonId + "/" + username + "?resubmitted=true"
    );
  }

  @PostMapping("/lesson/{lessonId}/{username}/cancel-booking")
  public String cancelBooking(
    @PathVariable Long lessonId,
    @PathVariable String username,
    @RequestParam Long bookingId,
    Model model
  ) {
    Optional<LessonBooking> bookingOpt = lessonBookingRepository.findById(
      bookingId
    );
    if (bookingOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Boeking niet gevonden");
      return "error";
    }

    LessonBooking booking = bookingOpt.get();
    booking.setStatus(LessonStatus.GEANNULEERD);
    lessonBookingRepository.save(booking);

    return "redirect:/lesson/" + lessonId + "/" + username;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model, Exception e) {
    model.addAttribute(
      "errorMessage",
      "Er is een fout opgetreden: " + e.getMessage()
    );
    return "error";
  }
}
