package com.webapp.hexit.controller;

import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchPageController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String searchUsers(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) List<String> filters, // Accept multiple filters
        Model model
    ) {
        List<User> users;

        // Initialize users list
        users = userService.getAllUsers();

        // Apply search query if provided
        if (query != null && !query.isBlank()) {
            users = userService.searchByUsername(query);
        }

        // Apply filters independently if provided
        if (filters != null && !filters.isEmpty()) {
            users = users
                .stream()
                .filter(user ->
                    filters
                        .stream()
                        .anyMatch(filter -> {
                            switch (filter.toLowerCase()) {
                                case "bedrijf":
                                    return user.getRole() == Role.BEDRIJF;
                                case "lessen":
                                    return user.getRole() == Role.DOCENT;
                                case "muziekant":
                                    return user.getRole() == Role.MUZIKANT;
                                default:
                                    return false;
                            }
                        })
                )
                .toList();
        }

        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("filters", filters);

        return "search";
    }
}
