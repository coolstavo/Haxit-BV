package com.webapp.hexit.controller;

import com.webapp.hexit.model.Genre;
import com.webapp.hexit.model.Instrument;
import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.MuzikantInstrument;
import com.webapp.hexit.model.Profile;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.GenreRepository;
import com.webapp.hexit.repository.InstrumentRepository;
import com.webapp.hexit.repository.MuzikantInstrumentRepository;
import com.webapp.hexit.repository.MuzikantRepository;
import com.webapp.hexit.repository.UserRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final MuzikantRepository muzikantRepository;
    private final MuzikantInstrumentRepository muzikantInstrumentRepository;
    private final InstrumentRepository instrumentRepository;
    private final GenreRepository genreRepository;

    public ProfileController(
        UserRepository userRepository,
        MuzikantRepository muzikantRepository,
        MuzikantInstrumentRepository muzikantInstrumentRepository,
        InstrumentRepository instrumentRepository,
        GenreRepository genreRepository
    ) {
        this.userRepository = userRepository;
        this.muzikantRepository = muzikantRepository;
        this.muzikantInstrumentRepository = muzikantInstrumentRepository;
        this.instrumentRepository = instrumentRepository;
        this.genreRepository = genreRepository;
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
                List<Genre> allGenres = genreRepository.findAll();
                model.addAttribute("allGenres", allGenres);
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

            List<MuzikantInstrument> muzikantInstruments =
                muzikantInstrumentRepository.findByMuzikantId(muzikant.getId());
            List<Instrument> allInstruments = instrumentRepository.findAll();

            model.addAttribute("muzikant", muzikant);
            model.addAttribute("muzikantInstruments", muzikantInstruments);
            model.addAttribute("allInstruments", allInstruments);
            List<Genre> allGenres = genreRepository.findAll();
            model.addAttribute("allGenres", allGenres);
            return "profile-edit-muzikant";
        }

        return handleError(model);
    }

    @PostMapping("/profile/save-muzikant")
    public String saveMuzikant(@ModelAttribute Muzikant muzikant) {
        Muzikant existingMuzikant = muzikantRepository
            .findById(muzikant.getId())
            .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

        existingMuzikant.setNaam(muzikant.getNaam());
        existingMuzikant.setLeeftijd(muzikant.getLeeftijd());

        List<Instrument> selectedInstruments = muzikant.getInstruments();
        existingMuzikant.setInstruments(selectedInstruments);

        List<Genre> selectedGenres = muzikant.getGenres();
        existingMuzikant.setGenres(selectedGenres);
        muzikantRepository.save(existingMuzikant);
        return "redirect:/profile/" + existingMuzikant.getUser().getUsername();
    }

    @PostMapping("/profile/add-instrument")
    public String addInstrument(
        @RequestParam Long muzikantId,
        @RequestParam Long instrumentId,
        @RequestParam String level
    ) {
        Muzikant muzikant = muzikantRepository
            .findById(muzikantId)
            .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

        Instrument instrument = instrumentRepository
            .findById(instrumentId)
            .orElseThrow(() ->
                new RuntimeException("Instrument niet gevonden")
            );

        MuzikantInstrument muzikantInstrument = new MuzikantInstrument();
        muzikantInstrument.setMuzikant(muzikant);
        muzikantInstrument.setInstrument(instrument);
        muzikantInstrument.setLevel(level);

        muzikantInstrumentRepository.save(muzikantInstrument);

        return "redirect:/profile/edit/" + muzikant.getUser().getUsername();
    }

    @PostMapping("/profile/remove-instrument")
    public String removeInstrument(@RequestParam Long muzikantInstrumentId) {
        Muzikant muzikant = muzikantInstrumentRepository
            .findById(muzikantInstrumentId)
            .orElseThrow(() ->
                new RuntimeException("MuzikantInstrument niet gevonden")
            )
            .getMuzikant();

        muzikantInstrumentRepository.deleteById(muzikantInstrumentId);
        return "redirect:/profile/edit/" + muzikant.getUser().getUsername();
    }

    @PostMapping("/profiel/add-genre")
    public String addGenre(
        @RequestParam Long muzikantId,
        @RequestParam Long genreId
    ) {
        Muzikant muzikant = muzikantRepository
            .findById(muzikantId)
            .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

        Genre genre = genreRepository
            .findById(genreId)
            .orElseThrow(() -> new RuntimeException("Genre niet gevonden"));

        muzikant.getGenres().add(genre);
        muzikantRepository.save(muzikant);

        return "redirect:/profile/edit/" + muzikant.getUser().getUsername();
    }

    @PostMapping("/profiel/remove-genre")
    public String removeGenre(
        @RequestParam Long muzikantId,
        @RequestParam Long genreId
    ) {
        Muzikant muzikant = muzikantRepository
            .findById(muzikantId)
            .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

        muzikant.getGenres().removeIf(genre -> genre.getId().equals(genreId));
        muzikantRepository.save(muzikant);

        return "redirect:/profile/edit/" + muzikant.getUser().getUsername();
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
