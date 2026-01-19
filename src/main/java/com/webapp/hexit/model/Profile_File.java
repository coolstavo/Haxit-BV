package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Profile_File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FileType fileType;

  private String beschrijving;

  @Column(nullable = false)
  private LocalDateTime uploadDate;

  @OneToOne(mappedBy = "profileFile", cascade = CascadeType.ALL)
  private Profile_Audio profileAudio;

  @OneToOne(mappedBy = "profileFile", cascade = CascadeType.ALL)
  private Profile_Video profileVideo;

  @OneToOne(mappedBy = "profileFile", cascade = CascadeType.ALL)
  private Profile_Link profileLink;

  public Profile_File() {
  }

  public Profile_File(
      User user,
      FileType fileType,
      String beschrijving,
      LocalDateTime uploadDate) {
    this.user = user;
    this.fileType = fileType;
    this.beschrijving = beschrijving;
    this.uploadDate = uploadDate;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public FileType getFileType() {
    return fileType;
  }

  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  public String getBeschrijving() {
    return beschrijving;
  }

  public void setBeschrijving(String beschrijving) {
    this.beschrijving = beschrijving;
  }

  public LocalDateTime getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(LocalDateTime uploadDate) {
    this.uploadDate = uploadDate;
  }

  public Profile_Audio getProfileAudio() {
    return profileAudio;
  }

  public void setProfileAudio(Profile_Audio profileAudio) {
    this.profileAudio = profileAudio;
  }

  public Profile_Video getProfileVideo() {
    return profileVideo;
  }

  public void setProfileVideo(Profile_Video profileVideo) {
    this.profileVideo = profileVideo;
  }

  public Profile_Link getProfileLink() {
    return profileLink;
  }

  public void setProfileLink(Profile_Link profileLink) {
    this.profileLink = profileLink;
  }

  // helper methodes
  public boolean isAudio() {
    return this.fileType == FileType.AUDIO;
  }

  public boolean isVideo() {
    return this.fileType == FileType.VIDEO;
  }

  public boolean isYoutube() {
    return this.profileLink != null && this.profileLink.isYoutube();
  }

  public boolean isSoundCloud() {
    return this.profileLink != null && this.profileLink.isSoundCloud();
  }

  public String getEmbedUrl() {
    if (this.profileLink != null) {
      return this.profileLink.getEmbedUrl();
    }
    return "";
  }
}