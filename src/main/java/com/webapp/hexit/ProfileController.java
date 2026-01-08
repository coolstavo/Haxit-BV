package com.webapp.hexit;

import com.webapp.hexit.model.Profile;
import com.webapp.hexit.repository.ProfileRepository;
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

    private final ProfileRepository profileRepository;

    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // PROFIEL BEKIJKEN
    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        Profile profile = profileRepository
            .findByUsername(username)
            .orElseGet(() -> {
                Profile p = new Profile();
                p.setUsername(username);
                return p;
            });

        model.addAttribute("profile", profile);
        return "profile";
    }

    @GetMapping("/profileadd/{username}")
    public String profileAdd(@PathVariable String username, Model model) {
        Profile profile = profileRepository
            .findByUsername(username)
            .orElseGet(() -> {
                Profile p = new Profile();
                p.setUsername(username);
                return p;
            });

        model.addAttribute("profile", profile);
        return "profileadd";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute Profile profile) {
        profileRepository.save(profile);
        return "redirect:/profile/" + profile.getUsername();
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
