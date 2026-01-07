package com.webapp.hexit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ProfileController {

  @GetMapping("/profile/{username}")
  public String profile(@PathVariable String username, Model model) {
    if (username == null || username.isBlank()) {
      return "redirect:/?loginRequired=true";
    }

    model.addAttribute("username", username);
    return "profile";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model, Exception ex) {
    model.addAttribute(
      "errorMessage",
      "Er is een fout opgetreden! We konden uw verzoek niet verwerken. Probeer het later opnieuw."
    );
    return "error";
  }
}
