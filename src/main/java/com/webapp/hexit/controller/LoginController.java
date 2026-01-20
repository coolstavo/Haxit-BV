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
    @RequestParam(required = false) String type
  ) {
    if (username == null || username.isBlank()) {
      return "redirect:/login-page";
    }

    // Maak gebruiker aan als deze niet bestaat (voor docenten)
    if ("docent".equals(type)) {
      User user = userRepository.findByUsername(username).orElse(null);
      if (user == null) {
        user = new User(username, Role.DOCENT);
        userRepository.save(user);

        Docent docent = new Docent(user);
        docentRepository.save(docent);
      }
      return "redirect:/profile/" + username;
    } else if ("bedrijf".equals(type)) {
      User user = userRepository.findByUsername(username).orElse(null);
      if (user == null) {
        user = new User(username, Role.BEDRIJF);
        userRepository.save(user);

        Company company = new Company(user);
        companyRepository.save(company);
      }

      return "redirect:/profile/" + username;
    } else {
      // default: muzikant
      // Maak User en Muzikant aan als ze niet bestaan
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
  }

  @GetMapping("/login-page")
  public String loginPage() {
    return "login";
  }
}
