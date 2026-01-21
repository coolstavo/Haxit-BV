package com.webapp.hexit.controller;

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
        @RequestParam(required = false) String filter,
        Model model
    ) {
        List<User> users;

        if (query == null || query.isBlank()) {
            users = userService.getAllUsers();
        } else {
            users = userService.searchByUsername(query);
        }

        model.addAttribute("users", users);
        model.addAttribute("query", query);

        return "search";
    }
}
