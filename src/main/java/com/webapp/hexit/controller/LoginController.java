package com.webapp.hexit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login(
    @RequestParam String username,
    @RequestParam(required = false) String type
  ) {
    if (username == null || username.isBlank()) {
      return "redirect:/login-page";
    }

    // Route naar de juiste profielpagina op basis van type
    if ("bedrijf".equals(type)) {
      return "redirect:/profile/bedrijf/" + username;
    } else if ("docent".equals(type)) {
      return "redirect:/profile/docent/" + username;
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
