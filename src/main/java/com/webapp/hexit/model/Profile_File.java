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

  public Profile_File() {}

  public Profile_File(
    User user,
    FileType fileType,
    String beschrijving,
    LocalDateTime uploadDate
  ) {
    this.user = user;
    this.fileType = fileType;
    this.beschrijving = beschrijving;
    this.uploadDate = uploadDate;
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
}
