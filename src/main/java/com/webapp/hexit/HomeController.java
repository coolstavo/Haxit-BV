package com.webapp.hexit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@Controller
public class HomeController {

    // Event model mockup
    public record Event(String title, String description, double lat, double lng, String type) {
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("description", description);
            map.put("lat", lat);
            map.put("lng", lng);
            map.put("type", type);
            return map;
        }
    }

    private List<Map<String, Object>> getDummyEvents() {

        // events vanuit EventController storage
        List<Map<String, Object>> allEvents = new ArrayList<>(EventController.getAllEvents());
        
        // if (allEvents.isEmpty()) {
            List<Event> events = new ArrayList<>();
            events.add(new Event("Gitarist Gezocht", "Rockband zoekt lead gitarist voor optredens.", 53.2194, 6.5665, "Opdracht"));
            events.add(new Event("Pianoles Aangeboden", "Beginners tot gevorderden welkom.", 53.1900, 6.5500, "Les"));
            events.add(new Event("Jazz Trio zoekt Drummer", "Voor bruiloften en partijen.", 53.2100, 6.5800, "Opdracht"));
            events.add(new Event("Studio Ruimte Te Huur", "Volledig ingerichte studio per uur.", 53.1800, 6.6000, "Bedrijf"));
            events.add(new Event("Vinyl DJ gezocht", "House/techno DJ voor zaterdagavond set.", 53.2250, 6.5600, "Opdracht"));
            events.add(new Event("Zangles Aangeboden", "Privélessen moderne zangtechniek.", 53.2050, 6.5750, "Les"));
            events.add(new Event("Bassist gezocht voor coverband", "We spelen pop/rock classics.", 53.1950, 6.5900, "Opdracht"));
            events.add(new Event("Saxofoon workshop", "Eendaagse workshop voor beginners.", 53.2300, 6.6100, "Les"));
            events.add(new Event("PA-set te huur", "Complete geluidsinstallatie per dag.", 53.2400, 6.5400, "Bedrijf"));
            events.add(new Event("Producer zoekt vocalist", "Samenwerkingen voor nieuwe EDM-track.", 53.2000, 6.5200, "Opdracht"));
            events.add(new Event("Kinderkoor repetities", "Nieuwe leden welkom, elke woensdag.", 53.1850, 6.5550, "Les"));
            events.add(new Event("Celloles aan huis", "Klassieke cellolessen op locatie.", 53.1750, 6.5850, "Les"));
            events.add(new Event("Drumstel verhuur", "Akoestisch en elektronisch beschikbaar.", 53.2600, 6.6000, "Bedrijf"));
            events.add(new Event("Open mic avond", "Meld je aan voor een optreden.", 53.2100, 6.5450, "Opdracht"));

            for (Event event : events) {
                allEvents.add(event.toMap());
            }
        // }
        
        return allEvents;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("events", getDummyEvents());
        model.addAttribute("username", "Gast");
        model.addAttribute("loginRequired", false);
        return "index";
    }

    @GetMapping("/{username}")
    public String homeWithUsername(@PathVariable String username,
                                   @RequestParam(name = "loginRequired", required = false) Boolean loginRequired,
                                   Model model) {
        String currentUser = (username != null && !username.isBlank()) ? username : "Gast";
        boolean mustLogin = loginRequired != null && loginRequired;

        model.addAttribute("events", getDummyEvents());
        model.addAttribute("username", currentUser);
        model.addAttribute("loginRequired", mustLogin);
        return "index";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(Model model, Exception ex) {
        model.addAttribute("errorMessage", "Er is een fout opgetreden! We konden uw verzoek niet verwerken. Probeer het later opnieuw.");
        return "error";
    }
}