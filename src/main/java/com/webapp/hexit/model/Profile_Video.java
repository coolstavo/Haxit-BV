package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Profile_Video {

  @Id
  private Long fileId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "file_id")
  private Profile_File profileFile;

  private String videoBestandPath;
  private String thumbnailPath;

  public Profile_Video() {}

  public Profile_Video(
    Profile_File profileFile,
    String videoBestandPath,
    String thumbnailPath
  ) {
    this.profileFile = profileFile;
    this.videoBestandPath = videoBestandPath;
    this.thumbnailPath = thumbnailPath;
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

  public String getVideoBestandPath() {
    return videoBestandPath;
  }

  public void setVideoBestandPath(String videoBestandPath) {
    this.videoBestandPath = videoBestandPath;
  }

  public String getThumbnailPath() {
    return thumbnailPath;
  }

  public void setThumbnailPath(String thumbnailPath) {
    this.thumbnailPath = thumbnailPath;
  }
}
