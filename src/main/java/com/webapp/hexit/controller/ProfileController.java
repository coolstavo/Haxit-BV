package com.webapp.hexit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import com.webapp.hexit.model.MediaItem;
import com.webapp.hexit.repository.MediaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private MediaItemRepository mediaItemRepository;

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        if (username == null || username.isBlank()) {
            return "redirect:/?loginRequired=true";
        }

        model.addAttribute("username", username);

        // Haal bestanden/content op uit db voor deze gebruiker
        List<MediaItem> userItems = mediaItemRepository.findByUsername(username);
        model.addAttribute("mediaList", userItems);
        
        return "profile";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(Model model, Exception ex) {
        model.addAttribute("errorMessage", "Er is een fout opgetreden! We konden uw verzoek niet verwerken. Probeer het later opnieuw.");
        return "error";
    }
}