package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String companyName;

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

  public Company(String companyName) {
    this.companyName = companyName;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
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
