package com.webapp.hexit.service;

import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchByUsername(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }
}
