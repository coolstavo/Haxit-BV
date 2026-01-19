package com.webapp.hexit.controller;

import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.Role;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.MuzikantRepository;
import com.webapp.hexit.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MuzikantRepository muzikantRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        if (savedUser.getRole() == Role.MUZIKANT) {
            Muzikant muzikant = new Muzikant();
            muzikant.setUser(savedUser);
            muzikantRepository.save(muzikant);
        }

        return savedUser;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
