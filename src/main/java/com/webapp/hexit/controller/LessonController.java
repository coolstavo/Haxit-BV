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
    // Fetch the lesson
    Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
    if (lessonOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Les niet gevonden");
      return "error";
    }

    Lesson lesson = lessonOpt.get();
    model.addAttribute("lesson", lesson);

    // Fetch the user
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();
    model.addAttribute("user", user);
    model.addAttribute("username", username);

    // Determine if user is teacher (docent) or student (muzikant)
    boolean isTeacher = user.getRole() == Role.DOCENT;
    boolean isStudent = user.getRole() == Role.MUZIKANT;

    model.addAttribute("isTeacher", isTeacher);
    model.addAttribute("isStudent", isStudent);

    // Fetch bookings based on role
    List<LessonBooking> bookings;

    if (isTeacher) {
      // Teacher sees all bookings for this lesson type
      bookings = lessonBookingRepository.findByLessonId(lessonId);
    } else if (isStudent) {
      // Student sees only their own bookings
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
      // Other roles see all bookings (read-only)
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
    // Fetch the lesson
    Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
    if (lessonOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Les niet gevonden");
      return "error";
    }

    Lesson lesson = lessonOpt.get();

    // Fetch the user
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    // Only students can request lessons
    if (user.getRole() != Role.MUZIKANT) {
      model.addAttribute(
        "errorMessage",
        "Alleen studenten kunnen lessen aanvragen"
      );
      return "error";
    }

    // Get the muzikant (student)
    Optional<Muzikant> muzikantOpt = muzikantRepository.findByUser(user);
    if (muzikantOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Student profiel niet gevonden");
      return "error";
    }

    Muzikant student = muzikantOpt.get();

    // Create the booking
    LessonBooking booking = new LessonBooking();
    booking.setStudent(student);
    booking.setLesson(lesson);
    booking.setStatus(LessonStatus.AANGEVRAAGD);
    booking.setAcceptedByStudent(true); // Student is requesting, so they accept
    booking.setAcceptedByDocent(false);

    // Add proposed times
    List<LocalDateTime> proposals = new ArrayList<>();
    if (proposal1 != null) proposals.add(proposal1);
    if (proposal2 != null) proposals.add(proposal2);
    if (proposal3 != null) proposals.add(proposal3);
    booking.setLessonProposals(proposals);

    lessonBookingRepository.save(booking);

    return "redirect:/lesson/" + lessonId + "/" + username + "?success=true";
  }

  @PostMapping("/lesson/{lessonId}/{username}/update-approval")
  public String updateApproval(
    @PathVariable Long lessonId,
    @PathVariable String username,
    @RequestParam Long bookingId,
    @RequestParam boolean approved,
    Model model
  ) {
    // Fetch the user
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    // Fetch the booking
    Optional<LessonBooking> bookingOpt = lessonBookingRepository.findById(
      bookingId
    );
    if (bookingOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Boeking niet gevonden");
      return "error";
    }

    LessonBooking booking = bookingOpt.get();

    // Check user role and update appropriate field
    if (user.getRole() == Role.DOCENT) {
      // Verify this teacher owns the lesson
      if (
        booking.getLesson().getDocent().getUser().getId().equals(user.getId())
      ) {
        booking.setAcceptedByDocent(approved);

        // Update status if both approved
        if (booking.isAcceptedByStudent() && approved) {
          booking.setStatus(LessonStatus.GEACCEPTEERD);
        }
      } else {
        model.addAttribute("errorMessage", "Geen toegang tot deze boeking");
        return "error";
      }
    } else if (user.getRole() == Role.MUZIKANT) {
      // Verify this student owns the booking
      Optional<Muzikant> muzikantOpt = muzikantRepository.findByUser(user);
      if (
        muzikantOpt.isPresent() &&
        booking.getStudent().getId().equals(muzikantOpt.get().getId())
      ) {
        booking.setAcceptedByStudent(approved);

        // Update status if both approved
        if (approved && booking.isAcceptedByDocent()) {
          booking.setStatus(LessonStatus.GEACCEPTEERD);
        }
      } else {
        model.addAttribute("errorMessage", "Geen toegang tot deze boeking");
        return "error";
      }
    } else {
      model.addAttribute(
        "errorMessage",
        "Geen rechten om goedkeuring te wijzigen"
      );
      return "error";
    }

    lessonBookingRepository.save(booking);

    return "redirect:/lesson/" + lessonId + "/" + username;
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
