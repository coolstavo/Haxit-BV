package com.webapp.hexit.controller;

import com.webapp.hexit.model.Genre;
import com.webapp.hexit.model.Instrument;
import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.GenreRepository;
import com.webapp.hexit.repository.InstrumentRepository;
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
  private final InstrumentRepository instrumentRepository;
  private final GenreRepository genreRepository;

  public JamApiController(
    JamRepository jamRepository,
    UserRepository userRepository,
    InstrumentRepository instrumentRepository,
    GenreRepository genreRepository
  ) {
    this.jamRepository = jamRepository;
    this.userRepository = userRepository;
    this.instrumentRepository = instrumentRepository;
    this.genreRepository = genreRepository;
  }

  @GetMapping("/all")
  public List<Jam> getAllJams() {
    return jamRepository.findAll();
  }

  @PostMapping("/add")
  public ResponseEntity<Jam> addJam(@RequestBody JamRequest jamRequest) {
    if (jamRequest.getTitle() == null || jamRequest.getDescription() == null) {
      return ResponseEntity.badRequest().build();
    }

    if (jamRequest.getMuzikantUserId() == null) {
      return ResponseEntity.badRequest().build();
    }

    // Find the user
    Optional<User> userOpt = userRepository.findById(
      jamRequest.getMuzikantUserId()
    );
    if (userOpt.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    // Create the jam
    Jam jam = new Jam(
      jamRequest.getTitle(),
      jamRequest.getDescription(),
      jamRequest.getLat(),
      jamRequest.getLng(),
      userOpt.get()
    );

    // Save the jam first to get an ID
    Jam saved = jamRepository.save(jam);

    // Add instruments
    if (jamRequest.getInstrumentIds() != null) {
      for (Long instrumentId : jamRequest.getInstrumentIds()) {
        Optional<Instrument> instrument = instrumentRepository.findById(
          instrumentId
        );
        if (instrument.isPresent()) {
          saved.addInstrument(instrument.get());
        }
      }
    }

    // Add genres
    if (jamRequest.getGenreIds() != null) {
      for (Long genreId : jamRequest.getGenreIds()) {
        Optional<Genre> genre = genreRepository.findById(genreId);
        if (genre.isPresent()) {
          saved.addGenre(genre.get());
        }
      }
    }

    // Save again with instruments and genres
    saved = jamRepository.save(saved);
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

  // Request DTO for Jam creation
  static class JamRequest {

    private String title;
    private String description;
    private double lat;
    private double lng;
    private Long muzikantUserId;
    private List<Long> instrumentIds;
    private List<Long> genreIds;

    public JamRequest() {}

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public double getLat() {
      return lat;
    }

    public void setLat(double lat) {
      this.lat = lat;
    }

    public double getLng() {
      return lng;
    }

    public void setLng(double lng) {
      this.lng = lng;
    }

    public Long getMuzikantUserId() {
      return muzikantUserId;
    }

    public void setMuzikantUserId(Long muzikantUserId) {
      this.muzikantUserId = muzikantUserId;
    }

    public List<Long> getInstrumentIds() {
      return instrumentIds;
    }

    public void setInstrumentIds(List<Long> instrumentIds) {
      this.instrumentIds = instrumentIds;
    }

    public List<Long> getGenreIds() {
      return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
      this.genreIds = genreIds;
    }
  }
}
