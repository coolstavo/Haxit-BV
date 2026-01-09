package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Event_Like {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  public Event_Like() {}

  public Event_Like(User user, Event event, LocalDateTime createdAt) {
    this.user = user;
    this.event = event;
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

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
