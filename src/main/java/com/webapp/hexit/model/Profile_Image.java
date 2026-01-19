package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Profile_Image {

  @Id
  private Long fileId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "file_id")
  private Profile_File profileFile;

  private String imageBestandPath;
  private boolean isProfilePicture;

  public Profile_Image() {}

  public Profile_Image(
    Profile_File profileFile,
    String imageBestandPath,
    boolean isProfilePicture
  ) {
    this.profileFile = profileFile;
    this.imageBestandPath = imageBestandPath;
    this.isProfilePicture = isProfilePicture;
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

  public String getImageBestandPath() {
    return imageBestandPath;
  }

  public void setImageBestandPath(String imageBestandPath) {
    this.imageBestandPath = imageBestandPath;
  }

  public boolean isProfilePicture() {
    return isProfilePicture;
  }

  public void setProfilePicture(boolean profilePicture) {
    isProfilePicture = profilePicture;
  }
}
