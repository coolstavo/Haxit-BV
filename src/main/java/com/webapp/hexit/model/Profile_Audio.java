package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Profile_Audio {

  @Id
  private Long fileId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "file_id")
  private Profile_File profileFile;

  private String audioBestandPath;
  private int durationSeconds;

  public Profile_Audio() {}

  public Profile_Audio(
    Profile_File profileFile,
    String audioBestandPath,
    int durationSeconds
  ) {
    this.profileFile = profileFile;
    this.audioBestandPath = audioBestandPath;
    this.durationSeconds = durationSeconds;
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

  public String getAudioBestandPath() {
    return audioBestandPath;
  }

  public void setAudioBestandPath(String audioBestandPath) {
    this.audioBestandPath = audioBestandPath;
  }

  public int getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(int durationSeconds) {
    this.durationSeconds = durationSeconds;
  }
}
