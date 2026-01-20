package com.webapp.hexit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Docent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  private String specialisatie; // Bijv. "Klassieke muziek", "Jazz"

  @Column(columnDefinition = "TEXT")
  private String biografie; // Uitgebreide beschrijving

  private Integer ervaringsjaren; // Aantal jaren onderwijservaring

  @Column(columnDefinition = "TEXT")
  private String kwalificaties; // Diploma's, certificaten, etc.

  @OneToMany(
    mappedBy = "docent",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @JsonIgnore
  private List<Lesson> lessons;

  public Docent() {}

  public Docent(User user) {
    this.user = user;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getNaam() {
    return user != null ? user.getUsername() : null;
  }

  public String getSpecialisatie() {
    return specialisatie;
  }

  public void setSpecialisatie(String specialisatie) {
    this.specialisatie = specialisatie;
  }

  public String getBiografie() {
    return biografie;
  }

  public void setBiografie(String biografie) {
    this.biografie = biografie;
  }

  public Integer getErvaringsjaren() {
    return ervaringsjaren;
  }

  public void setErvaringsjaren(Integer ervaringsjaren) {
    this.ervaringsjaren = ervaringsjaren;
  }

  public String getKwalificaties() {
    return kwalificaties;
  }

  public void setKwalificaties(String kwalificaties) {
    this.kwalificaties = kwalificaties;
  }

  public List<Lesson> getLessons() {
    return lessons;
  }

  public void setLessons(List<Lesson> lessons) {
    this.lessons = lessons;
  }
}
