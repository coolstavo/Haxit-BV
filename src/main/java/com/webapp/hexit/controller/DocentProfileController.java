package com.webapp.hexit.controller;

import com.webapp.hexit.model.Docent;
import com.webapp.hexit.model.Lesson;
import com.webapp.hexit.repository.DocentRepository;
import com.webapp.hexit.repository.LessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
public class DocentProfileController {

  private final DocentRepository docentRepository;
  private final LessonRepository lessonRepository;

  public DocentProfileController(DocentRepository docentRepository, LessonRepository lessonRepository) {
    this.docentRepository = docentRepository;
    this.lessonRepository = lessonRepository;
  }

  @GetMapping("/profile/docent/{docentnaam}")
  public String getDocentProfile(@PathVariable String docentnaam, Model model) {
    Optional<Docent> docent = docentRepository.findByNaam(docentnaam);
    
    if (docent.isPresent()) {
      List<Lesson> lessons = lessonRepository.findByDocentId(docent.get().getId());
      model.addAttribute("docent", docent.get());
      model.addAttribute("lessons", lessons);
    } else {
      model.addAttribute("lessons", List.of());
    }
    
    model.addAttribute("docentName", docentnaam);
    model.addAttribute("username", docentnaam);
    model.addAttribute("userRole", "DOCENT");
    model.addAttribute("edit", false);
    return "docent-profile";
  }

  @GetMapping("/profile/docent/{docentnaam}/edit")
  public String editDocentProfile(@PathVariable String docentnaam, Model model) {
    Optional<Docent> docent = docentRepository.findByNaam(docentnaam);
    
    if (docent.isPresent()) {
      List<Lesson> lessons = lessonRepository.findByDocentId(docent.get().getId());
      model.addAttribute("docent", docent.get());
      model.addAttribute("lessons", lessons);
    } else {
      model.addAttribute("lessons", List.of());
    }
    
    model.addAttribute("docentName", docentnaam);
    model.addAttribute("username", docentnaam);
    model.addAttribute("userRole", "DOCENT");
    model.addAttribute("edit", true);
    model.addAttribute("instruments", new String[]{
      "Gitaar", "Piano", "Zang", "Viool", "Cello", "Trompet", "Saxofoon", 
      "Drums", "Fluit", "Klarinet", "Harp", "Mandoline"
    });
    model.addAttribute("levels", new String[]{
      "Beginner", "Intermediate", "Advanced"
    });
    model.addAttribute("lessonForms", new String[]{
      "Fysiek op locatie docent",
      "Fysiek bij student thuis",
      "Online/Video-call"
    });
    model.addAttribute("rateTypes", new String[]{
      "per 30 minuten",
      "per uur"
    });
    return "docent-profile";
  }

  @PostMapping("/profile/docent/save-profile")
  public String saveDocentProfile(
    @RequestParam String naam,
    @RequestParam(required = false) String specialisatie,
    @RequestParam(required = false) String biografie,
    @RequestParam(required = false) Integer ervaringsjaren,
    @RequestParam(required = false) String kwalificaties,
    Model model
  ) {
    try {
      Optional<Docent> docent = docentRepository.findByNaam(naam);
      
      if (docent.isEmpty()) {
        model.addAttribute("errorMessage", "Docent niet gevonden");
        return "error";
      }
      
      Docent d = docent.get();
      d.setSpecialisatie(specialisatie);
      d.setBiografie(biografie);
      d.setErvaringsjaren(ervaringsjaren);
      d.setKwalificaties(kwalificaties);
      
      docentRepository.save(d);
      return "redirect:/profile/docent/" + naam;
    } catch (Exception e) {
      model.addAttribute("errorMessage", "Fout bij opslaan van profiel");
      return "error";
    }
  }

  @PostMapping("/profile/docent/save-lesson")
  public String saveLessonOffering(
    @RequestParam String docentName,
    @RequestParam String instrument,
    @RequestParam(required = false) String[] levels,
    @RequestParam String lessonForm,
    @RequestParam Double rate,
    @RequestParam String rateType,
    @RequestParam(required = false) String description,
    Model model
  ) {
    try {
      Optional<Docent> docent = docentRepository.findByNaam(docentName);
      
      if (docent.isEmpty()) {
        model.addAttribute("errorMessage", "Docent niet gevonden");
        return "error";
      }
      
      Lesson lesson = new Lesson(docent.get());
      lesson.setInstrument(instrument);
      lesson.setLessonForm(lessonForm);
      lesson.setRate(rate);
      lesson.setRateType(rateType);
      lesson.setDescription(description);
      
      if (levels != null && levels.length > 0) {
        lesson.setLevels(String.join(", ", levels));
      } else {
        lesson.setLevels("");
      }
      
      lessonRepository.save(lesson);
      return "redirect:/profile/docent/" + docentName + "/edit";
    } catch (Exception e) {
      model.addAttribute("errorMessage", "Fout bij opslaan van les");
      return "error";
    }
  }

  @GetMapping("/profile/docent/delete-lesson/{id}")
  public String deleteLesson(@PathVariable Long id, @RequestParam String docentName) {
    lessonRepository.deleteById(id);
    return "redirect:/profile/docent/" + docentName + "/edit";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleError(Model model) {
    model.addAttribute("errorMessage", "Er is een fout opgetreden!");
    return "error";
  }
}
