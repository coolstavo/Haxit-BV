package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Docent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String naam;

  public Docent() {}

  public Docent(User user, String naam) {
    this.user = user;
    this.naam = naam;
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
    return naam;
  }

  public void setNaam(String naam) {
    this.naam = naam;
  }
}
