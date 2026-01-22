package com.webapp.hexit.controller;

import com.webapp.hexit.model.Genre;
import com.webapp.hexit.model.Instrument;
import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.MuzikantInstrument;
import com.webapp.hexit.model.Profile_File;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.GenreRepository;
import com.webapp.hexit.repository.InstrumentRepository;
import com.webapp.hexit.repository.JamRepository;
import com.webapp.hexit.repository.MuzikantInstrumentRepository;
import com.webapp.hexit.repository.MuzikantRepository;
import com.webapp.hexit.repository.ProfileFileRepository;
import com.webapp.hexit.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MuzikantProfileController {

  private final UserRepository userRepository;
  private final MuzikantRepository muzikantRepository;
  private final MuzikantInstrumentRepository muzikantInstrumentRepository;
  private final InstrumentRepository instrumentRepository;
  private final GenreRepository genreRepository;
  private final ProfileFileRepository profileFileRepository;
  private final JamRepository jamRepository;

  // Pad waar profielfoto's worden opgeslagen
  private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/profilepics/";

  public MuzikantProfileController(
    UserRepository userRepository,
    MuzikantRepository muzikantRepository,
    MuzikantInstrumentRepository muzikantInstrumentRepository,
    InstrumentRepository instrumentRepository,
    GenreRepository genreRepository,
    ProfileFileRepository profileFileRepository,
    JamRepository jamRepository
  ) {
    this.userRepository = userRepository;
    this.muzikantRepository = muzikantRepository;
    this.muzikantInstrumentRepository = muzikantInstrumentRepository;
    this.instrumentRepository = instrumentRepository;
    this.genreRepository = genreRepository;
    this.profileFileRepository = profileFileRepository;
    this.jamRepository = jamRepository;
  }

  public String getProfile(String username, Model model) {
    User profile = userRepository.findByUsername(username).orElse(null);

    if (profile == null) return handleError(model);

    Muzikant muzikant = muzikantRepository.findByUser(profile).orElse(null);
    if (muzikant != null) {

      List<MuzikantInstrument> muzikantInstruments =
        muzikantInstrumentRepository.findByMuzikantId(muzikant.getId());
      List<Instrument> allInstruments = instrumentRepository.findAll();

      model.addAttribute("muzikant", muzikant);
      model.addAttribute("muzikantInstruments", muzikantInstruments);
      model.addAttribute("allInstruments", allInstruments);
      List<Genre> allGenres = genreRepository.findAll();
      model.addAttribute("allGenres", allGenres);

      // Content ophalen van gebruiker
      List<Profile_File> userContent =
        profileFileRepository.findByUser_UsernameOrderByUploadDateDesc(
          username
        );
      model.addAttribute("mediaBestanden", userContent);

      // Get jams created by this muzikant
      List<Jam> jams = jamRepository.findByMuzikantUser(profile);
      model.addAttribute("jams", jams);

      return "profile-muzikant";
    }
    return handleError(model);
  }

  public String getProfileEdit(String username, Model model) {
    User profile = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

    Muzikant muzikant = muzikantRepository
      .findByUser(profile)
      .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

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

  @GetMapping("/profile/edit")
  public String getProfileEditSession(HttpSession session, Model model) {
    // Get current user from session
    Object usernameObj = session.getAttribute("currentUsername");
    if (usernameObj == null) {
      return "redirect:/login-page";
    }
    String username = usernameObj.toString();
    return getProfileEdit(username, model);
  }

  @PostMapping("/profile/change-profile-pic")
  public String saveMuzikantProfilePic(
    @ModelAttribute Muzikant muzikant,
    @RequestParam("imageFile") MultipartFile file
  ) throws IOException {

    Muzikant existingMuzikant = muzikantRepository
      .findById(muzikant.getId())
      .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

    if (!file.isEmpty()) {
      Path uploadPath = Path.of(UPLOAD_DIR);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      // Achterhaal bestandsextensie 
      String originalName = file.getOriginalFilename();
      String extension = "";
      if (originalName != null && originalName.contains(".")) {
          extension = originalName.substring(originalName.lastIndexOf("."));
      }

      // Maak bestandsnaam uniek
      String uniqueName = existingMuzikant.getUser().getUsername() + "_" + UUID.randomUUID().toString() + extension;

      // Sla fysieke bestand op
      Path path = Paths.get(UPLOAD_DIR + uniqueName);
      Files.write(path, file.getBytes());

      // Sla path op in database
      existingMuzikant.setProfilePic("/uploads/profilepics/" + uniqueName);
    }
    muzikantRepository.save(existingMuzikant);

    return "redirect:/profile/" + existingMuzikant.getUser().getUsername();
  }

  @PostMapping("/profile/save-muzikant")
  public String saveMuzikantProfile(@ModelAttribute Muzikant muzikant) {
    Muzikant existingMuzikant = muzikantRepository
      .findById(muzikant.getId())
      .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

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
      .orElseThrow(() -> new RuntimeException("Instrument niet gevonden"));

    MuzikantInstrument muzikantInstrument = new MuzikantInstrument();
    muzikantInstrument.setMuzikant(muzikant);
    muzikantInstrument.setInstrument(instrument);
    muzikantInstrument.setLevel(level);

    muzikantInstrumentRepository.save(muzikantInstrument);

    return ("redirect:/profile/" + muzikant.getUser().getUsername() + "/edit");
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
    return ("redirect:/profile/" + muzikant.getUser().getUsername() + "/edit");
  }

  @PostMapping("/profile/add-genre")
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

    return ("redirect:/profile/" + muzikant.getUser().getUsername() + "/edit");
  }

  @PostMapping("/profile/remove-genre")
  public String removeGenre(
    @RequestParam Long muzikantId,
    @RequestParam Long genreId
  ) {
    Muzikant muzikant = muzikantRepository
      .findById(muzikantId)
      .orElseThrow(() -> new RuntimeException("Muzikant niet gevonden"));

    muzikant.getGenres().removeIf(genre -> genre.getId().equals(genreId));
    muzikantRepository.save(muzikant);

    return ("redirect:/profile/" + muzikant.getUser().getUsername() + "/edit");
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
