package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Lesson {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "docent_id", nullable = false)
  private Docent docent;

  @Column(nullable = false)
  private String instrument;

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

  @Column(nullable = false)
  private LocalDateTime createdAt;

  public Lesson() {}

  public Lesson(Docent docent) {
    this.docent = docent;
    this.createdAt = LocalDateTime.now();
  }

  // Getters and Setters
  public Long getId() { return id; }
  
  public Docent getDocent() { return docent; }
  public void setDocent(Docent docent) { this.docent = docent; }

  public String getInstrument() { return instrument; }
  public void setInstrument(String instrument) { this.instrument = instrument; }

  public String getLevels() { return levels; }
  public void setLevels(String levels) { this.levels = levels; }

  public String getLessonForm() { return lessonForm; }
  public void setLessonForm(String lessonForm) { this.lessonForm = lessonForm; }

  public Double getRate() { return rate; }
  public void setRate(Double rate) { this.rate = rate; }

  public String getRateType() { return rateType; }
  public void setRateType(String rateType) { this.rateType = rateType; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}