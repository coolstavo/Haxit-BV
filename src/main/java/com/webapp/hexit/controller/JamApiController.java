package com.webapp.hexit.controller;

import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.JamRepository;
import com.webapp.hexit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jams")
public class JamApiController {

  private final JamRepository jamRepository;
  private final UserRepository userRepository;

  public JamApiController(
    JamRepository jamRepository,
    UserRepository userRepository
  ) {
    this.jamRepository = jamRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/all")
  public List<Jam> getAllJams() {
    return jamRepository.findAll();
  }

  @PostMapping("/add")
  public ResponseEntity<Jam> addJam(@RequestBody Jam jam) {
    if (jam.getTitle() == null || jam.getDescription() == null) {
      return ResponseEntity.badRequest().build();
    }

    if (
      jam.getMuzikantUser() == null || jam.getMuzikantUser().getId() == null
    ) {
      return ResponseEntity.badRequest().build();
    }

    Jam saved = jamRepository.save(jam);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/muzikant/{muzikantUserId}")
  public List<Jam> getMuzikantJams(@PathVariable Long muzikantUserId) {
    return jamRepository.findByMuzikantUserId(muzikantUserId);
  }

  @DeleteMapping("/{jamId}")
  public ResponseEntity<Void> deleteJam(@PathVariable Long jamId) {
    Optional<Jam> jamOpt = jamRepository.findById(jamId);
    if (jamOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    jamRepository.delete(jamOpt.get());
    return ResponseEntity.ok().build();
  }
}
