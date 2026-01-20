package com.webapp.hexit.controller;

import com.webapp.hexit.model.Company;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.CompanyRepository;
import com.webapp.hexit.repository.UserRepository;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CompanyProfileController {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
  private static final String[] ALLOWED_MIME_TYPES = {
    "image/jpeg",
    "image/png",
  };

  public CompanyProfileController(
    CompanyRepository companyRepository,
    UserRepository userRepository
  ) {
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
  }

  /**
   * Public method for unified profile routing
   */
  public String getCompanyProfile(String username, Model model) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      return handleError(model);
    }

    Optional<Company> company = companyRepository.findByUser(user);

    if (company.isPresent()) {
      model.addAttribute("company", company.get());
      model.addAttribute("hasData", true);
    } else {
      Company newCompany = new Company(user);
      model.addAttribute("company", newCompany);
      model.addAttribute("hasData", false);
    }

    model.addAttribute("companyName", username);
    model.addAttribute("username", username);
    model.addAttribute("userRole", "BEDRIJF");
    model.addAttribute("edit", false);
    return "company-profile";
  }

  /**
   * Public method for unified edit routing
   */
  public String editCompanyProfile(String username, Model model) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      return handleError(model);
    }

    Optional<Company> company = companyRepository.findByUser(user);

    if (company.isPresent()) {
      model.addAttribute("company", company.get());
      model.addAttribute("hasData", true);
    } else {
      Company newCompany = new Company(user);
      model.addAttribute("company", newCompany);
      model.addAttribute("hasData", false);
    }

    model.addAttribute("companyName", username);
    model.addAttribute("username", username);
    model.addAttribute("userRole", "BEDRIJF");
    model.addAttribute("edit", true);
    model.addAttribute(
      "genresOptions",
      new String[] {
        "Jazz",
        "Pop",
        "Klassiek",
        "Rock",
        "Hip Hop",
        "Elektronisch",
        "R&B",
        "Country",
        "Blues",
        "Metal",
      }
    );
    return "company-profile";
  }

  /**
   * Original method for backwards compatibility (by bedrijfnaam)
   */
  @GetMapping("/profile/bedrijf/{bedrijfnaam}/edit")
  public String editCompanyProfileByName(
    @PathVariable String bedrijfnaam,
    Model model
  ) {
    User user = userRepository.findByUsername(bedrijfnaam).orElse(null);

    if (user == null) {
      return handleError(model);
    }

    Optional<Company> company = companyRepository.findByUser(user);

    if (company.isPresent()) {
      model.addAttribute("company", company.get());
      model.addAttribute("hasData", true);
    } else {
      Company newCompany = new Company(user);
      model.addAttribute("company", newCompany);
      model.addAttribute("hasData", false);
    }

    model.addAttribute("companyName", bedrijfnaam);
    model.addAttribute("username", bedrijfnaam);
    model.addAttribute("userRole", "BEDRIJF");
    model.addAttribute("edit", true);
    model.addAttribute(
      "genresOptions",
      new String[] {
        "Jazz",
        "Pop",
        "Klassiek",
        "Rock",
        "Hip Hop",
        "Elektronisch",
        "R&B",
        "Country",
        "Blues",
        "Metal",
      }
    );
    return "company-profile";
  }

  @PostMapping("/profile/bedrijf/save")
  public String saveCompanyProfile(
    @RequestParam String companyName,
    @RequestParam String location,
    @RequestParam(required = false) String websiteUrl,
    @RequestParam String aboutUs,
    @RequestParam(required = false) MultipartFile logo,
    @RequestParam(required = false) String[] genres,
    @RequestParam String musiciansWanted,
    Model model
  ) {
    try {
      User user = userRepository.findByUsername(companyName).orElse(null);

      if (user == null) {
        user = new User(companyName, Role.BEDRIJF);
        userRepository.save(user);
      }

      Optional<Company> existing = companyRepository.findByUser(user);
      Company company = existing.orElse(new Company(user));

      company.setLocation(location);
      company.setWebsiteUrl(
        websiteUrl != null && !websiteUrl.isBlank() ? websiteUrl : null
      );
      company.setAboutUs(aboutUs);
      company.setMusiciansWanted(musiciansWanted);

      // Convert genres array to comma-separated string
      if (genres != null && genres.length > 0) {
        company.setGenres(String.join(", ", genres));
      } else {
        company.setGenres("");
      }

      // Handle logo upload
      if (logo != null && !logo.isEmpty()) {
        if (logo.getSize() > MAX_FILE_SIZE) {
          model.addAttribute("error", "Bestand is groter dan 5MB");
          return "redirect:/profile/" + companyName + "/edit?error=filesize";
        }

        boolean validType = false;
        for (String mimeType : ALLOWED_MIME_TYPES) {
          if (mimeType.equals(logo.getContentType())) {
            validType = true;
            break;
          }
        }

        if (!validType) {
          model.addAttribute("error", "Alleen JPG en PNG zijn toegestaan");
          return "redirect:/profile/" + companyName + "/edit?error=filetype";
        }

        company.setLogo(logo.getBytes());
        company.setLogoFileName(logo.getOriginalFilename());
        company.setLogoMimeType(logo.getContentType());
      }

      companyRepository.save(company);
      return "redirect:/profile/" + companyName;
    } catch (IOException e) {
      model.addAttribute("errorMessage", "Fout bij bestandsupload");
      return "error";
    }
  }

  @GetMapping("/api/company/logo/{id}")
  @ResponseBody
  public ResponseEntity<byte[]> getLogo(@PathVariable Long id) {
    return companyRepository
      .findById(id)
      .filter(c -> c.getLogo() != null)
      .map(c ->
        ResponseEntity.ok()
          .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + c.getLogoFileName() + "\""
          )
          .contentType(MediaType.parseMediaType(c.getLogoMimeType()))
          .body(c.getLogo())
      )
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model) {
    model.addAttribute("errorMessage", "Er is een fout opgetreden!");
    return "error";
  }
}
