package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Instrument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String naam;

  public Instrument() {}

  public Instrument(String naam) {
    this.naam = naam;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public String getNaam() {
    return naam;
  }

  public void setNaam(String naam) {
    this.naam = naam;
  }
}
