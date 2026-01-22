package com.webapp.hexit.controller;

import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ProfileController {

  private final UserRepository userRepository;
  private final MuzikantProfileController muzikantProfileController;
  private final DocentProfileController docentProfileController;
  private final CompanyProfileController companyProfileController;

  public ProfileController(
    UserRepository userRepository,
    MuzikantProfileController muzikantProfileController,
    DocentProfileController docentProfileController,
    CompanyProfileController companyProfileController
  ) {
    this.userRepository = userRepository;
    this.muzikantProfileController = muzikantProfileController;
    this.docentProfileController = docentProfileController;
    this.companyProfileController = companyProfileController;
  }

  @GetMapping("/profile/{username}")
  public String profile(@PathVariable String username, Model model) {
    User profile = userRepository.findByUsername(username).orElse(null);

    if (profile == null) throw new RuntimeException("Gebruiker niet gevonden: " + username);

    switch (profile.getRole()) {
      case MUZIKANT:
        return muzikantProfileController.getProfile(username, model);
      case DOCENT:
        return docentProfileController.getDocentProfile(username, model);
      case BEDRIJF:
        return companyProfileController.getCompanyProfile(username, model);
      default:
        throw new RuntimeException("Rol van " + username + " niet herkend.");
    }
  }

  @GetMapping("/profile/{username}/edit")
  public String profileEdit(@PathVariable String username, Model model) {
    User profile = userRepository.findByUsername(username).orElse(null);

    if (profile == null) throw new RuntimeException("Gebruiker niet gevonden: " + username);

    switch (profile.getRole()) {
      case MUZIKANT:
        return muzikantProfileController.getProfileEdit(username, model);
      case DOCENT:
        return docentProfileController.editDocentProfile(username, model);
      case BEDRIJF:
        return companyProfileController.editCompanyProfile(username, model);
      default:
        throw new RuntimeException("Rol van " + username + " niet herkend.");
    }
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Exception ex, Model model) {

    ex.printStackTrace();

    model.addAttribute(
      "errorMessage",
      "Er is een fout opgetreden! We konden uw verzoek niet verwerken."
    );
    return "error";
  }
}
