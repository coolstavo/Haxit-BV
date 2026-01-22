package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  private double lat;
  private double lng;

  @ManyToOne
  @JoinColumn(name = "company_user_id")
  private User companyUser;

  // Backwards compatibility with old userId column
  @Column(name = "user_id")
  private Long userId;

  @OneToMany(
    mappedBy = "event",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<EventInstrument> eventInstruments = new ArrayList<>();

  @OneToMany(
    mappedBy = "event",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<EventGenre> eventGenres = new ArrayList<>();

  public Event() {} // default constructor required by JPA

  public Event(
    String title,
    String description,
    double lat,
    double lng,
    User companyUser
  ) {
    this.title = title;
    this.description = description;
    this.lat = lat;
    this.lng = lng;
    this.companyUser = companyUser;
    if (companyUser != null) {
      this.userId = companyUser.getId();
    }
  }

  // Constructor for backwards compatibility
  public Event(
    String title,
    String description,
    double lat,
    double lng,
    Long userId
  ) {
    this.title = title;
    this.description = description;
    this.lat = lat;
    this.lng = lng;
    this.userId = userId;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

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

  public User getCompanyUser() {
    return companyUser;
  }

  public void setCompanyUser(User companyUser) {
    this.companyUser = companyUser;
    if (companyUser != null) {
      this.userId = companyUser.getId();
    }
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<EventInstrument> getEventInstruments() {
    return eventInstruments;
  }

  public void setEventInstruments(List<EventInstrument> eventInstruments) {
    this.eventInstruments = eventInstruments;
  }

  public List<EventGenre> getEventGenres() {
    return eventGenres;
  }

  public void setEventGenres(List<EventGenre> eventGenres) {
    this.eventGenres = eventGenres;
  }

  /**
   * Helper method to get instruments for display purposes
   */
  public List<Instrument> getInstruments() {
    return eventInstruments
      .stream()
      .map(EventInstrument::getInstrument)
      .collect(Collectors.toList());
  }

  /**
   * Helper method to get genres for display purposes
   */
  public List<Genre> getGenres() {
    return eventGenres
      .stream()
      .map(EventGenre::getGenre)
      .collect(Collectors.toList());
  }

  /**
   * Helper method to add an instrument to the event
   */
  public void addInstrument(Instrument instrument) {
    EventInstrument eventInstrument = new EventInstrument(this, instrument);
    eventInstruments.add(eventInstrument);
  }

  /**
   * Helper method to add a genre to the event
   */
  public void addGenre(Genre genre) {
    EventGenre eventGenre = new EventGenre(this, genre);
    eventGenres.add(eventGenre);
  }
}
