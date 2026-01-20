package com.webapp.hexit.controller;

import com.webapp.hexit.model.Company;
import com.webapp.hexit.model.Docent;
import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.CompanyRepository;
import com.webapp.hexit.repository.DocentRepository;
import com.webapp.hexit.repository.MuzikantRepository;
import com.webapp.hexit.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

  private final DocentRepository docentRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final MuzikantRepository muzikantRepository;

  public LoginController(
    DocentRepository docentRepository,
    UserRepository userRepository,
    MuzikantRepository muzikantRepository,
    CompanyRepository companyRepository
  ) {
    this.docentRepository = docentRepository;
    this.userRepository = userRepository;
    this.muzikantRepository = muzikantRepository;
    this.companyRepository = companyRepository;
  }

  @GetMapping("/login")
  public String login(
    @RequestParam String username,
    @RequestParam(required = false) String type,
    HttpSession session
  ) {
    if (username == null || username.isBlank()) {
      return "redirect:/login-page";
    }

    session.setAttribute("currentUsername", username);

    if ("docent".equals(type)) {
      session.setAttribute("currentUserRole", "DOCENT");

      User user = userRepository.findByUsername(username).orElse(null);
      if (user == null) {
        user = new User(username, Role.DOCENT);
        userRepository.save(user);

        Docent docent = new Docent(user);
        docentRepository.save(docent);
      }
      return "redirect:/profile/docent/" + username;
    }

    if ("bedrijf".equals(type)) {
      session.setAttribute("currentUserRole", "BEDRIJF");

      User user = userRepository.findByUsername(username).orElse(null);
      if (user == null) {
        user = new User(username, Role.BEDRIJF);
        userRepository.save(user);

        Company company = new Company(user);
        companyRepository.save(company);
      }
      return "redirect:/profile/" + username;
    }

    // default muzikant
    session.setAttribute("currentUserRole", "MUZIKANT");

    User user = userRepository.findByUsername(username).orElse(null);
    if (user == null) {
      user = new User(username, Role.MUZIKANT);
      userRepository.save(user);

      Muzikant muzikant = new Muzikant();
      muzikant.setUser(user);
      muzikantRepository.save(muzikant);
    }

    return "redirect:/profile/" + username;
  }

  @GetMapping("/login-page")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }
}
