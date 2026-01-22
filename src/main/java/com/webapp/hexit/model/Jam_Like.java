package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Jam_Like {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "jam_id", nullable = false)
  private Jam jam;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  public Jam_Like() {}

  public Jam_Like(User user, Jam jam, LocalDateTime createdAt) {
    this.user = user;
    this.jam = jam;
    this.createdAt = createdAt;
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

  public Jam getJam() {
    return jam;
  }

  public void setJam(Jam jam) {
    this.jam = jam;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
