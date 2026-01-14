package com.webapp.hexit.controller;

import com.webapp.hexit.model.Docent;
import com.webapp.hexit.model.User;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.repository.DocentRepository;
import com.webapp.hexit.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

  private final DocentRepository docentRepository;
  private final UserRepository userRepository;

  public LoginController(DocentRepository docentRepository, UserRepository userRepository) {
    this.docentRepository = docentRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/login")
  public String login(
    @RequestParam String username,
    @RequestParam(required = false) String type
  ) {
    if (username == null || username.isBlank()) {
      return "redirect:/login-page";
    }

    // Maak gebruiker aan als deze niet bestaat (voor docenten)
    if ("docent".equals(type)) {
      if (docentRepository.findByNaam(username).isEmpty()) {
        User user = new User(username, Role.DOCENT);
        userRepository.save(user);
        
        Docent docent = new Docent(user, username);
        docentRepository.save(docent);
      }
      return "redirect:/profile/docent/" + username;
    } else if ("bedrijf".equals(type)) {
      return "redirect:/profile/bedrijf/" + username;
    } else {
      // default: muzikant
      return "redirect:/profile/" + username;
    }
  }

  @GetMapping("/login-page")
  public String loginPage() {
    return "login";
  }
}
