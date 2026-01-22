package com.webapp.hexit.controller;

import com.webapp.hexit.model.*;
import com.webapp.hexit.repository.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LessonController {

  private final LessonRepository lessonRepository;
  private final LessonBookingRepository lessonBookingRepository;
  private final UserRepository userRepository;
  private final MuzikantRepository muzikantRepository;
  private final LessonLikeRepository lessonLikeRepository;
  private final LessonCommentRepository lessonCommentRepository;

  public LessonController(
    LessonRepository lessonRepository,
    LessonBookingRepository lessonBookingRepository,
    UserRepository userRepository,
    MuzikantRepository muzikantRepository,
    LessonLikeRepository lessonLikeRepository,
    LessonCommentRepository lessonCommentRepository
  ) {
    this.lessonRepository = lessonRepository;
    this.lessonBookingRepository = lessonBookingRepository;
    this.userRepository = userRepository;
    this.muzikantRepository = muzikantRepository;
    this.lessonLikeRepository = lessonLikeRepository;
    this.lessonCommentRepository = lessonCommentRepository;
  }

  @GetMapping("/lesson/{lessonId}")
  public String getLessonPage(
    @PathVariable Long lessonId,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    // Get likes and comments
    long likeCount = lessonLikeRepository.countByLessonId(lessonId);
    boolean userHasLiked = lessonLikeRepository.existsByUserAndLesson(
      user,
      lesson
    );
    List<Lesson_Comment> comments =
      lessonCommentRepository.findByLessonIdOrderByCreatedAtDesc(lessonId);

    model.addAttribute("likeCount", likeCount);
    model.addAttribute("userHasLiked", userHasLiked);
    model.addAttribute("comments", comments);

    return "lesson";
  }

  @PostMapping("/lesson/{lessonId}/like")
  @Transactional
  public String toggleLike(
    @PathVariable Long lessonId,
    @RequestParam(required = false) String redirect,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    // Toggle like
    if (lessonLikeRepository.existsByUserAndLesson(user, lesson)) {
      lessonLikeRepository.deleteByUserAndLesson(user, lesson);
    } else {
      Lesson_Like like = new Lesson_Like(user, lesson, LocalDateTime.now());
      lessonLikeRepository.save(like);
    }

    // Redirect back to home page if requested, otherwise go to lesson detail page
    if ("home".equals(redirect)) {
      return "redirect:/home";
    }
    return "redirect:/lesson/" + lessonId;
  }

  @PostMapping("/lesson/{lessonId}/comment")
  public String addComment(
    @PathVariable Long lessonId,
    @RequestParam String content,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

    if (content == null || content.trim().isEmpty()) {
      return "redirect:/lesson/" + lessonId;
    }

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

    Lesson_Comment comment = new Lesson_Comment(
      user,
      lesson,
      content.trim(),
      LocalDateTime.now()
    );
    lessonCommentRepository.save(comment);

    return "redirect:/lesson/" + lessonId;
  }

  @Transactional
  @PostMapping("/lesson/{lessonId}/comment/{commentId}/delete")
  public String deleteComment(
    @PathVariable Long lessonId,
    @PathVariable Long commentId,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

    Optional<Lesson_Comment> commentOpt = lessonCommentRepository.findById(
      commentId
    );
    if (commentOpt.isEmpty()) {
      return "redirect:/lesson/" + lessonId;
    }

    Lesson_Comment comment = commentOpt.get();

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    // Only allow comment deletion by the comment author
    if (!comment.getUser().getId().equals(user.getId())) {
      model.addAttribute(
        "errorMessage",
        "Geen toegang om deze opmerking te verwijderen"
      );
      return "error";
    }

    lessonCommentRepository.delete(comment);

    return "redirect:/lesson/" + lessonId;
  }

  @PostMapping("/lesson/{lessonId}/request")
  public String requestLesson(
    @PathVariable Long lessonId,
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
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    return "redirect:/lesson/" + lessonId + "?success=true";
  }

  @PostMapping("/lesson/{lessonId}/accept")
  public String acceptBooking(
    @PathVariable Long lessonId,
    @RequestParam Long bookingId,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime chosenTime,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    return "redirect:/lesson/" + lessonId;
  }

  @PostMapping("/lesson/{lessonId}/reject")
  public String rejectBooking(
    @PathVariable Long lessonId,
    @RequestParam Long bookingId,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    return "redirect:/lesson/" + lessonId;
  }

  @PostMapping("/lesson/{lessonId}/update-proposals")
  public String updateProposals(
    @PathVariable Long lessonId,
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
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    return ("redirect:/lesson/" + lessonId + "?resubmitted=true");
  }

  @PostMapping("/lesson/{lessonId}/cancel-booking")
  public String cancelBooking(
    @PathVariable Long lessonId,
    @RequestParam Long bookingId,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

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

    return "redirect:/lesson/" + lessonId;
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
