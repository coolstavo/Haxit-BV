package com.webapp.hexit.controller;

import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.Jam_Comment;
import com.webapp.hexit.model.Jam_Like;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.JamCommentRepository;
import com.webapp.hexit.repository.JamLikeRepository;
import com.webapp.hexit.repository.JamRepository;
import com.webapp.hexit.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jam")
public class JamController {

  private final JamRepository jamRepository;
  private final UserRepository userRepository;
  private final JamLikeRepository jamLikeRepository;
  private final JamCommentRepository jamCommentRepository;

  public JamController(
    JamRepository jamRepository,
    UserRepository userRepository,
    JamLikeRepository jamLikeRepository,
    JamCommentRepository jamCommentRepository
  ) {
    this.jamRepository = jamRepository;
    this.userRepository = userRepository;
    this.jamLikeRepository = jamLikeRepository;
    this.jamCommentRepository = jamCommentRepository;
  }

  @GetMapping("/{jamId}")
  public String getJamPage(
    @PathVariable Long jamId,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

    Optional<Jam> jamOpt = jamRepository.findById(jamId);
    if (jamOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Jam niet gevonden");
      return "error";
    }

    Jam jam = jamOpt.get();
    model.addAttribute("jam", jam);

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();
    model.addAttribute("user", user);

    // Check if user is the jam owner (muzikant)
    boolean isOwner =
      jam.getMuzikantUser() != null &&
      jam.getMuzikantUser().getId().equals(user.getId());
    model.addAttribute("isOwner", isOwner);

    // Get likes and comments
    long likeCount = jamLikeRepository.countByJamId(jamId);
    boolean userHasLiked = jamLikeRepository.existsByUserAndJam(user, jam);
    List<Jam_Comment> comments =
      jamCommentRepository.findByJamIdOrderByCreatedAtDesc(jamId);

    model.addAttribute("likeCount", likeCount);
    model.addAttribute("userHasLiked", userHasLiked);
    model.addAttribute("comments", comments);

    return "jam";
  }

  @PostMapping("/{jamId}/like")
  public String toggleLike(
    @PathVariable Long jamId,
    @RequestParam(required = false) String redirect,
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String types,
    @RequestParam(required = false) String instruments,
    @RequestParam(required = false) String genres,
    @RequestParam(required = false) Boolean likedByMe,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

    Optional<Jam> jamOpt = jamRepository.findById(jamId);
    if (jamOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Jam niet gevonden");
      return "error";
    }

    Jam jam = jamOpt.get();

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    // Toggle like
    if (jamLikeRepository.existsByUserAndJam(user, jam)) {
      jamLikeRepository.deleteByUserAndJam(user, jam);
    } else {
      Jam_Like like = new Jam_Like(user, jam, LocalDateTime.now());
      jamLikeRepository.save(like);
    }

    // Redirect back to home page if requested, otherwise go to jam detail page
    if ("home".equals(redirect)) {
      StringBuilder redirectUrl = new StringBuilder("redirect:/home?");
      boolean hasParams = false;

      if (name != null && !name.isBlank()) {
        redirectUrl.append("name=").append(name);
        hasParams = true;
      }
      if (types != null && !types.isBlank()) {
        if (hasParams) redirectUrl.append("&");
        redirectUrl.append("types=").append(types);
        hasParams = true;
      }
      if (instruments != null && !instruments.isBlank()) {
        if (hasParams) redirectUrl.append("&");
        redirectUrl.append("instruments=").append(instruments);
        hasParams = true;
      }
      if (genres != null && !genres.isBlank()) {
        if (hasParams) redirectUrl.append("&");
        redirectUrl.append("genres=").append(genres);
        hasParams = true;
      }
      if (likedByMe != null && likedByMe) {
        if (hasParams) redirectUrl.append("&");
        redirectUrl.append("likedByMe=true");
      }

      return redirectUrl.toString();
    }
    return "redirect:/jam/" + jamId;
  }

  @PostMapping("/{jamId}/comment")
  public String addComment(
    @PathVariable Long jamId,
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
      return "redirect:/jam/" + jamId;
    }

    Optional<Jam> jamOpt = jamRepository.findById(jamId);
    if (jamOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Jam niet gevonden");
      return "error";
    }

    Jam jam = jamOpt.get();

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    Jam_Comment comment = new Jam_Comment(
      user,
      jam,
      content.trim(),
      LocalDateTime.now()
    );
    jamCommentRepository.save(comment);

    return "redirect:/jam/" + jamId;
  }

  @Transactional
  @PostMapping("/{jamId}/comment/{commentId}/delete")
  public String deleteComment(
    @PathVariable Long jamId,
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

    Optional<Jam_Comment> commentOpt = jamCommentRepository.findById(commentId);
    if (commentOpt.isEmpty()) {
      return "redirect:/jam/" + jamId;
    }

    Jam_Comment comment = commentOpt.get();

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

    jamCommentRepository.delete(comment);

    return "redirect:/jam/" + jamId;
  }

  @PostMapping("/{jamId}/delete")
  public String deleteJam(
    @PathVariable Long jamId,
    HttpSession session,
    Model model
  ) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();

    Optional<Jam> jamOpt = jamRepository.findById(jamId);
    if (jamOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Jam niet gevonden");
      return "error";
    }

    Jam jam = jamOpt.get();

    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      model.addAttribute("errorMessage", "Gebruiker niet gevonden");
      return "error";
    }

    User user = userOpt.get();

    // Verify user is the owner
    if (
      jam.getMuzikantUser() == null ||
      !jam.getMuzikantUser().getId().equals(user.getId())
    ) {
      model.addAttribute(
        "errorMessage",
        "Geen toegang om deze jam te verwijderen"
      );
      return "error";
    }

    jamRepository.delete(jam);
    return "redirect:/profile/" + username;
  }
}
