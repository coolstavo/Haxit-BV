package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.Event_Comment;
import com.webapp.hexit.model.Event_Like;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.EventCommentRepository;
import com.webapp.hexit.repository.EventLikeRepository;
import com.webapp.hexit.repository.EventRepository;
import com.webapp.hexit.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/event")
public class EventController {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final EventLikeRepository eventLikeRepository;
  private final EventCommentRepository eventCommentRepository;

  public EventController(
    EventRepository eventRepository,
    UserRepository userRepository,
    EventLikeRepository eventLikeRepository,
    EventCommentRepository eventCommentRepository
  ) {
    this.eventRepository = eventRepository;
    this.userRepository = userRepository;
    this.eventLikeRepository = eventLikeRepository;
    this.eventCommentRepository = eventCommentRepository;
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

    // Get likes and comments
    long likeCount = eventLikeRepository.countByEventId(eventId);
    boolean userHasLiked = eventLikeRepository.existsByUserAndEvent(
      user,
      event
    );
    List<Event_Comment> comments =
      eventCommentRepository.findByEventIdOrderByCreatedAtDesc(eventId);

    model.addAttribute("likeCount", likeCount);
    model.addAttribute("userHasLiked", userHasLiked);
    model.addAttribute("comments", comments);

    return "event";
  }

  @PostMapping("/{eventId}/{username}/like")
  public String toggleLike(
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

    // Toggle like
    if (eventLikeRepository.existsByUserAndEvent(user, event)) {
      eventLikeRepository.deleteByUserAndEvent(user, event);
    } else {
      Event_Like like = new Event_Like(user, event, LocalDateTime.now());
      eventLikeRepository.save(like);
    }

    return "redirect:/event/" + eventId + "/" + username;
  }

  @PostMapping("/{eventId}/{username}/comment")
  public String addComment(
    @PathVariable Long eventId,
    @PathVariable String username,
    @RequestParam String content,
    Model model
  ) {
    if (content == null || content.trim().isEmpty()) {
      return "redirect:/event/" + eventId + "/" + username;
    }

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

    Event_Comment comment = new Event_Comment(
      user,
      event,
      content.trim(),
      LocalDateTime.now()
    );
    eventCommentRepository.save(comment);

    return "redirect:/event/" + eventId + "/" + username;
  }

  @Transactional
  @PostMapping("/{eventId}/{username}/comment/{commentId}/delete")
  public String deleteComment(
    @PathVariable Long eventId,
    @PathVariable String username,
    @PathVariable Long commentId,
    Model model
  ) {
    Optional<Event_Comment> commentOpt = eventCommentRepository.findById(
      commentId
    );
    if (commentOpt.isEmpty()) {
      return "redirect:/event/" + eventId + "/" + username;
    }

    Event_Comment comment = commentOpt.get();

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

    eventCommentRepository.delete(comment);

    return "redirect:/event/" + eventId + "/" + username;
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
