package com.webapp.hexit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompanyProfileController {

  @GetMapping("/profile/bedrijf/{bedrijfnaam}")
  public String getCompanyProfile(@PathVariable String bedrijfnaam, Model model) {
    model.addAttribute("companyName", bedrijfnaam);
    model.addAttribute("username", bedrijfnaam);
    model.addAttribute("edit", false);
    return "company-profile";
  }

  @GetMapping("/profile/bedrijf/{bedrijfnaam}/edit")
  public String editCompanyProfile(@PathVariable String bedrijfnaam, Model model) {
    model.addAttribute("companyName", bedrijfnaam);
    model.addAttribute("username", bedrijfnaam);
    model.addAttribute("edit", true);
    model.addAttribute("genresOptions", new String[]{
      "Jazz", "Pop", "Klassiek", "Rock", "Hip Hop", "Elektronisch", "R&B", "Country", "Blues", "Metal"
    });
    return "company-profile";
  }

  @PostMapping("/profile/bedrijf/save")
  public String saveCompanyProfile(
    @RequestParam String companyName,
    @RequestParam String location,
    @RequestParam String websiteUrl,
    @RequestParam String aboutUs,
    @RequestParam(required = false) String genres,
    @RequestParam String musiciansWanted,
    Model model
  ) {
    // Hier zou je normaal de data opslaan in database
    System.out.println("Bedrijf opgeslagen: " + companyName);
    return "redirect:/profile/bedrijf/" + companyName;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model, Exception ex) {
    model.addAttribute("errorMessage", "Er is een fout opgetreden!");
    return "error";
  }
}
