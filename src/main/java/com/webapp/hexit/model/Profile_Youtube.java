package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Profile_Youtube {

  @Id
  private Long fileId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "file_id")
  private Profile_File profileFile;

  private String youtubeUrl;

  public Profile_Youtube() {}

  public Profile_Youtube(Profile_File profileFile, String youtubeUrl) {
    this.profileFile = profileFile;
    this.youtubeUrl = youtubeUrl;
  }

  // Getters and Setters
  public Long getFileId() {
    return fileId;
  }

  public Profile_File getProfileFile() {
    return profileFile;
  }

  public void setProfileFile(Profile_File profileFile) {
    this.profileFile = profileFile;
  }

  public String getYoutubeUrl() {
    return youtubeUrl;
  }

  public void setYoutubeUrl(String youtubeUrl) {
    this.youtubeUrl = youtubeUrl;
  }
}
