package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Jam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  private double lat;
  private double lng;

  @ManyToOne
  @JoinColumn(name = "muzikant_user_id")
  private User muzikantUser;

  // Backwards compatibility with old userId column
  @Column(name = "user_id")
  private Long userId;

  @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Jam_Like> likes = new ArrayList<>();

  @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Jam_Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JamInstrument> jamInstruments = new ArrayList<>();

  @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JamGenre> jamGenres = new ArrayList<>();

  public Jam() {} // default constructor required by JPA

  public Jam(
    String title,
    String description,
    double lat,
    double lng,
    User muzikantUser
  ) {
    this.title = title;
    this.description = description;
    this.lat = lat;
    this.lng = lng;
    this.muzikantUser = muzikantUser;
    if (muzikantUser != null) {
      this.userId = muzikantUser.getId();
    }
  }

  // Constructor for backwards compatibility
  public Jam(
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

  public User getMuzikantUser() {
    return muzikantUser;
  }

  public void setMuzikantUser(User muzikantUser) {
    this.muzikantUser = muzikantUser;
    if (muzikantUser != null) {
      this.userId = muzikantUser.getId();
    }
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<Jam_Like> getLikes() {
    return likes;
  }

  public void setLikes(List<Jam_Like> likes) {
    this.likes = likes;
  }

  public List<Jam_Comment> getComments() {
    return comments;
  }

  public void setComments(List<Jam_Comment> comments) {
    this.comments = comments;
  }

  public List<JamInstrument> getJamInstruments() {
    return jamInstruments;
  }

  public void setJamInstruments(List<JamInstrument> jamInstruments) {
    this.jamInstruments = jamInstruments;
  }

  public List<JamGenre> getJamGenres() {
    return jamGenres;
  }

  public void setJamGenres(List<JamGenre> jamGenres) {
    this.jamGenres = jamGenres;
  }

  /**
   * Helper method to get instruments for display purposes
   */
  public List<Instrument> getInstruments() {
    return jamInstruments
      .stream()
      .map(JamInstrument::getInstrument)
      .collect(Collectors.toList());
  }

  /**
   * Helper method to get genres for display purposes
   */
  public List<Genre> getGenres() {
    return jamGenres
      .stream()
      .map(JamGenre::getGenre)
      .collect(Collectors.toList());
  }

  /**
   * Helper method to add an instrument to the jam
   */
  public void addInstrument(Instrument instrument) {
    JamInstrument jamInstrument = new JamInstrument(this, instrument);
    jamInstruments.add(jamInstrument);
  }

  /**
   * Helper method to add a genre to the jam
   */
  public void addGenre(Genre genre) {
    JamGenre jamGenre = new JamGenre(this, genre);
    jamGenres.add(jamGenre);
  }
}
