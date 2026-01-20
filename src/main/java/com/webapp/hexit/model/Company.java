package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String location;
  private String websiteUrl;

  @Column(columnDefinition = "TEXT")
  private String aboutUs;

  @Lob
  private byte[] logo;

  private String logoFileName;
  private String logoMimeType;

  @Column(columnDefinition = "TEXT")
  private String genres;

  @Column(columnDefinition = "TEXT")
  private String musiciansWanted;

  public Company() {}

  public Company(User user) {
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

  public String getCompanyName() {
    return user != null ? user.getUsername() : null;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public String getAboutUs() {
    return aboutUs;
  }

  public void setAboutUs(String aboutUs) {
    this.aboutUs = aboutUs;
  }

  public byte[] getLogo() {
    return logo;
  }

  public void setLogo(byte[] logo) {
    this.logo = logo;
  }

  public String getLogoFileName() {
    return logoFileName;
  }

  public void setLogoFileName(String logoFileName) {
    this.logoFileName = logoFileName;
  }

  public String getLogoMimeType() {
    return logoMimeType;
  }

  public void setLogoMimeType(String logoMimeType) {
    this.logoMimeType = logoMimeType;
  }

  public String getGenres() {
    return genres;
  }

  public void setGenres(String genres) {
    this.genres = genres;
  }

  public String getMusiciansWanted() {
    return musiciansWanted;
  }

  public void setMusiciansWanted(String musiciansWanted) {
    this.musiciansWanted = musiciansWanted;
  }
}
