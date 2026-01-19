package com.webapp.hexit.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.persistence.*;

@Entity
public class Profile_Link {

  @Id
  private Long fileId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "file_id")
  private Profile_File profileFile;

  private String url;

  public Profile_Link() {
  }

  public Profile_Link(Profile_File profileFile, String url) {
    this.profileFile = profileFile;
    this.url = url;
  }

  // Getters and Setters
  public Long getFileId() {
    return fileId;
  }

  public void setFileId(Long fileId) {
    this.fileId = fileId;
  }

  public Profile_File getProfileFile() {
    return profileFile;
  }

  public void setProfileFile(Profile_File profileFile) {
    this.profileFile = profileFile;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  // helper methode voor embedden youtube/soundcloud url

  public boolean isYoutube() {
    return url != null && (url.contains("youtube.com") || url.contains("youtu.be"));
  }

  public boolean isSoundCloud() {
    return url != null && url.contains("soundcloud.com");
  }

  public String getEmbedUrl() {
    if (url == null)
      return "";

    // Youtube Logica
    if (isYoutube()) {
      String videoId = "";
      // Haal video ID uit de URL
      if (url.contains("v=")) {
        String[] parts = url.split("v=");
        if (parts.length > 1) {
          videoId = parts[1];
          // Overbodige parameters verwijderen
          int ampPos = videoId.indexOf('&');
          if (ampPos != -1)
            videoId = videoId.substring(0, ampPos);
        }
      } // Haal id uit youtu.be link
      else if (url.contains("youtu.be/")) {
        String[] parts = url.split("youtu.be/");
        if (parts.length > 1)
          videoId = parts[1];
      }
      return "https://www.youtube.com/embed/" + videoId;
    }

    // SoundCloud Logica
    if (isSoundCloud()) {
      try {
        // URL encoderen sinds soundcloud player dit vereist
        String encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        return "https://w.soundcloud.com/player/?url=" + encoded
            + "&color=%23ff5500&auto_play=false&show_comments=true";
      } catch (Exception e) {
        return "";
      }
    }
    return "";
  }
}
