package com.webapp.hexit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.webapp.hexit.model.MediaItem;
import com.webapp.hexit.repository.MediaItemRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


@Controller
public class MediaController {

    @Autowired
    private MediaItemRepository mediaItemRepository;
    private static String UPLOAD_DIR = "uploads/";
    
    @PostMapping("/upload")
    public String uploadContent(@RequestParam("username") String username, 
                                @RequestParam("description") String description,
                                @RequestParam(value = "link", required = false) String link,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                RedirectAttributes redirectAttributes) {

        boolean isFileEmpty = (file == null || file.isEmpty());
        boolean isLinkEmpty = (link == null || link.isBlank());
        // Validatie
        if (isFileEmpty && isLinkEmpty) {
            redirectAttributes.addFlashAttribute("errorMessage", "Je moet een bestand kiezen of een link invullen.");
            return "redirect:/profile/" + username;
        }
        
        // Database-object maken
        MediaItem newItem = new MediaItem();
        newItem.setDescription(description);
        newItem.setUsername(username);

        try {
            // Bestand uploaden
            if (!isFileEmpty) {
                String contentType = file.getContentType();
                // Bestandstype controleren 
                if (contentType == null || (!contentType.contains("audio") && !contentType.contains("video"))) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Dit bestandstype is niet toegestaan! Alleen audio en video kan geüpload worden.");
                    return "redirect:/profile/" + username; 
                }
                // Bestandsnaam schoonmaken (spaties en speciale tekens vervangen met _) en uniek maken
                String originalName = file.getOriginalFilename();
                String extension = "";
                int i = originalName.lastIndexOf('.');
                if (i > 0) {
                    extension = originalName.substring(i);
                    originalName = originalName.substring(0, i);
                }
                String cleanName = originalName.replaceAll("[^a-zA-Z0-9]", "_") 
                + "_" + System.currentTimeMillis() + extension;

                // Map maken indien die er nog niet is
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                
                // Bestand opslaan op server
                Path path = Paths.get(UPLOAD_DIR + cleanName);
                Files.write(path, file.getBytes());

                // Pad opslaan in database
                newItem.setLink("/uploads/" + cleanName);
                
                // Type bepalen
                String type = "UNKNOWN";
                if (contentType.contains("audio")) type = "AUDIO";
                if (contentType.contains("video")) type = "VIDEO";
                newItem.setType(type);
            } 
            // Externe link (YouTube/SoundCloud)
            else if (!isLinkEmpty) {
                if (!link.contains("youtube.com") && !link.contains("youtu.be") && !link.contains("soundcloud.com")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Alleen links van YouTube en SoundCloud zijn toegestaan.");
                    return "redirect:/profile/" + username; 
                }
                newItem.setLink(link);
                newItem.setType("LINK");
            }

            // Opslaan in database
            mediaItemRepository.save(newItem);

            redirectAttributes.addFlashAttribute("successMessage", "Uw item is succesvol geüpload!");

        } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("errorMessage", "Er ging iets mis bij het opslaan.");
    }

        return "redirect:/profile/" + username;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(Model model, Exception ex) {
        model.addAttribute("errorMessage", "Er is een fout opgetreden! We konden uw verzoek niet verwerken. Probeer het later opnieuw.");
        return "error";
    }

    
    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) { 
    
    // Haal item op uit database
    MediaItem item = mediaItemRepository.findById(id).orElse(null);

    if (item != null) {
        // Verwijder fysieke bestand
        if (item.getLink() != null && item.getLink().startsWith("/uploads/")) {
            File file = new File(System.getProperty("user.dir") + item.getLink());
            if (file.exists()) {
                file.delete();
            }
        }
        // Verwijder uit de database
        mediaItemRepository.delete(item);
        // Succesbericht
        String successMessage = "Het item \"" + item.getDescription() + "\" is succesvol verwijderd!";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
    }

        return "redirect:/profile/" + item.getUsername();
    }
}
