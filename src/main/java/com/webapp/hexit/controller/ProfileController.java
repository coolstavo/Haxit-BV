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

        Muzikant existingMuzikant = muzikantRepository
            .findById(muzikant.getId())
            .orElseThrow(() ->
                new RuntimeException(
                    "Muzikant met ID " + muzikant.getId() + " niet gevonden"
                )
            );

        existingMuzikant.setNaam(muzikant.getNaam());
        existingMuzikant.setLeeftijd(muzikant.getLeeftijd());

        muzikantRepository.save(existingMuzikant);

        return "redirect:/profile/" + existingMuzikant.getUser().getUsername();
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
