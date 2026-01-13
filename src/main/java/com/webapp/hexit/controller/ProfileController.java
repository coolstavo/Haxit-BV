package com.webapp.hexit.controller;

import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.Profile;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.MuzikantRepository;
import com.webapp.hexit.repository.UserRepository;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final MuzikantRepository muzikantRepository;

    public ProfileController(
        UserRepository userRepository,
        MuzikantRepository muzikantRepository
    ) {
        this.userRepository = userRepository;
        this.muzikantRepository = muzikantRepository;
    }

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        User profile = userRepository.findByUsername(username).orElse(null);

        if (profile == null) {
            return handleError(model);
        }

        Role userRole = profile.getRole();

        switch (userRole) {
            case Role.MUZIKANT:
                Muzikant muzikant = muzikantRepository
                    .findByUser(profile)
                    .orElse(null);

                if (muzikant != null) {
                    model.addAttribute("muzikant", muzikant);
                } else {
                    return handleError(model);
                }
                return "profile-muzikant";
            case Role.BEDRIJF:
            case Role.DOCENT:
            case Role.ADMIN:
            default:
                return handleError(model);
        }
    }

    @GetMapping("/profile/edit/{username}")
    public String profileEdit(@PathVariable String username, Model model) {
        User profile = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

        if (profile.getRole() == Role.MUZIKANT) {
            Muzikant muzikant = muzikantRepository
                .findByUser(profile)
                .orElseThrow(() ->
                    new RuntimeException("Muzikant niet gevonden")
                );

            model.addAttribute("muzikant", muzikant);
            return "profile-edit-muzikant";
        }

        return handleError(model);
    }

    @PostMapping("/profile/save-muzikant")
    public String saveMuzikant(
        @ModelAttribute("muzikant") Muzikant muzikant,
        Principal principal,
        Model model
    ) {
        System.out.println("DEBUG: Ontvangen ID: " + muzikant.getId());

        // 1. Controleer of de gebruiker wel is ingelogd
        if (principal == null) {
            System.out.println(
                "DEBUG: Geen principal gevonden! Gebruiker is niet ingelogd."
            );
            return "redirect:/login";
        }

        // 2. Haal de bestaande muzikant op uit de DB inclusief de User
        Muzikant existingMuzikant = muzikantRepository
            .findById(muzikant.getId())
            .orElseThrow(() ->
                new RuntimeException(
                    "Muzikant met ID " + muzikant.getId() + " niet gevonden"
                )
            );

        // 3. De cruciale beveiligingscheck
        String ingelogdeNaam = principal.getName();
        String eigenaarNaam = existingMuzikant.getUser().getUsername();

        System.out.println("DEBUG: Ingelogd als: " + ingelogdeNaam);
        System.out.println("DEBUG: Eigenaar van profiel: " + eigenaarNaam);

        if (!ingelogdeNaam.equals(eigenaarNaam)) {
            System.out.println(
                "DEBUG: Toegang geweigerd! Namen komen niet overeen."
            );
            model.addAttribute(
                "errorMessage",
                "Je mag alleen je eigen profiel bewerken."
            );
            return "error";
        }

        // 4. Update de velden
        existingMuzikant.setNaam(muzikant.getNaam());
        existingMuzikant.setLeeftijd(muzikant.getLeeftijd());

        // 5. Opslaan
        muzikantRepository.save(existingMuzikant);
        System.out.println("DEBUG: Opslaan geslaagd voor: " + eigenaarNaam);

        return "redirect:/profile/" + eigenaarNaam;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(Model model) {
        model.addAttribute(
            "errorMessage",
            "Er is een fout opgetreden! We konden uw verzoek niet verwerken."
        );
        return "error";
    }
}
