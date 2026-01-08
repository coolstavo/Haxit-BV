package com.webapp.hexit;

import com.webapp.hexit.model.Profile;
import com.webapp.hexit.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final ProfileRepository profileRepository;

    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

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

    @GetMapping("/profileadd")
    public String profileAdded() {
        return "profileadd";
    }

    @PostMapping("/profile/save")
    public String saveProfile(
        @ModelAttribute Profile profile,
        RedirectAttributes redirectAttributes
    ) {
        profileRepository.save(profile);

        // Voeg de velden toe die je nodig hebt in profileadd.html
        redirectAttributes.addFlashAttribute("username", profile.getUsername());
        redirectAttributes.addFlashAttribute("age", profile.getAge());
        redirectAttributes.addFlashAttribute(
            "instrument",
            profile.getInstrument()
        );
        redirectAttributes.addFlashAttribute("genres", profile.getGenres());
        redirectAttributes.addFlashAttribute(
            "education",
            profile.getEducation()
        );
        redirectAttributes.addFlashAttribute("goals", profile.getGoals());

        return "redirect:/profile";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(Model model, Exception ex) {
        model.addAttribute(
            "errorMessage",
            "Er is een fout opgetreden! We konden uw verzoek niet verwerken."
        );
        return "error";
    }
}
