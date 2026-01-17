package com.webapp.hexit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Lesson {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "docent_id", nullable = false)
  @JsonIgnoreProperties({ "lessons", "user", "biografie", "kwalificaties" })
  private Docent docent;

  @ManyToOne
  @JoinColumn(name = "instrument_id", nullable = false)
  @JsonIgnoreProperties({ "lessons" })
  private Instrument instrument;

  @Column(columnDefinition = "TEXT")
  private String levels; // comma-separated: Beginner, Intermediate, Advanced

  @Column(nullable = false)
  private String lessonForm; // Fysiek op locatie docent, Fysiek bij student thuis, Online/Video-call

  @Column(nullable = false)
  private Double rate; // numeriek tarief

  @Column(nullable = false)
  private String rateType; // per 30 minuten, per uur

  @Column(columnDefinition = "TEXT")
  private String description;

  // Locatie velden
  @Column(nullable = true)
  private String location; // Adres of locatienaam

  @Column(nullable = true)
  private Double lat; // Latitude

  @Column(nullable = true)
  private Double lng; // Longitude

  @Column(nullable = false)
  private LocalDateTime createdAt;

  public Lesson() {}

  public Lesson(Docent docent) {
    this.docent = docent;
    this.createdAt = LocalDateTime.now();
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public Docent getDocent() {
    return docent;
  }

  public void setDocent(Docent docent) {
    this.docent = docent;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  /**
   * Helper method to get instrument name for display purposes
   */
  public String getInstrumentName() {
    return instrument != null ? instrument.getNaam() : null;
  }

  public String getLevels() {
    return levels;
  }

  public void setLevels(String levels) {
    this.levels = levels;
  }

  public String getLessonForm() {
    return lessonForm;
  }

  public void setLessonForm(String lessonForm) {
    this.lessonForm = lessonForm;
  }

  public Double getRate() {
    return rate;
  }

  public void setRate(Double rate) {
    this.rate = rate;
  }

  public String getRateType() {
    return rateType;
  }

  public void setRateType(String rateType) {
    this.rateType = rateType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLng() {
    return lng;
  }

  public void setLng(Double lng) {
    this.lng = lng;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
