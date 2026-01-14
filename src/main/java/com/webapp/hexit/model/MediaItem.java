package com.webapp.hexit.model;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.persistence.*;

@Entity
@Table(name = "media_items")
public class MediaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String url; 
    private String type; // LINK, AUDIO, of VIDEO
    private String username;

    public MediaItem() {}

    public MediaItem(String description, String url, String type, String username) {
        this.description = description;
        this.url = url;
        this.type = type;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLink() { return url; }
    public void setLink(String link) { this.url = link; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Check of het een YouTube link is 
    public boolean isYoutube() {
        return this.url != null && (this.url.contains("youtube.com") || this.url.contains("youtu.be"));
    }

    // Maak embed-link voor iframe
    public String getYoutubeEmbedUrl() {
        if (url == null) return "";
        
        String videoId = "";
        
        if (url.contains("v=")) {
            String[] parts = url.split("v=");
            videoId = parts[1];
            int ampersandPosition = videoId.indexOf('&');
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition);
            }
        } else if (url.contains("youtu.be/")) {
            String[] parts = url.split("youtu.be/");
            videoId = parts[1];
        }

        // Return embed link
        return "https://www.youtube.com/embed/" + videoId;
    }

    // Check of het een SoundCloud link is
    public boolean isSoundCloud() {
        return this.url != null && this.url.contains("soundcloud.com");
    }

    // Maak de embed-link voor SoundCloud
    public String getSoundCloudEmbedUrl() {
        if (url == null) return "";
        
        try {
            // URL encoderen omdat SoundCloud dit vereist
            String encodedLink = URLEncoder.encode(this.url, StandardCharsets.UTF_8.toString());
            
            return "https://w.soundcloud.com/player/?url=" + encodedLink + "&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}