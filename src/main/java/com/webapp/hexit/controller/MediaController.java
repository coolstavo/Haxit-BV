package com.webapp.hexit.controller;

import com.webapp.hexit.model.*;
import com.webapp.hexit.repository.ProfileFileRepository;
import com.webapp.hexit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Controller
public class MediaController {

    @Autowired
    private ProfileFileRepository profileFileRepository;

    @Autowired
    private UserRepository userRepository;

    private static String UPLOAD_DIR = "uploads/";

    @GetMapping("/profile/{username}/upload")
    public String showUploadPage(@PathVariable String username, Model model) {
        model.addAttribute("username", username);
        return "profile-upload-muzikant";
    }

    @PostMapping("/upload")
    public String uploadContent(@RequestParam("username") String username,
            @RequestParam("description") String description,
            @RequestParam(value = "link", required = false) String link,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User niet gevonden"));

        boolean isFileEmpty = (file == null || file.isEmpty());
        boolean isLinkEmpty = (link == null || link.isBlank());
        // Validatie: check of beide niet leeg zijn
        if (isFileEmpty && isLinkEmpty) {
            redirectAttributes.addFlashAttribute("warningMessage",
                    "Let op: Je moet een bestand kiezen of een link invullen voordat je kunt uploaden.");
            return "redirect:/profile/upload/" + username;
        }

        try {
            // Bestand opslaan (Audio/Video)
            if (!isFileEmpty) {
                String contentType = file.getContentType();

                // Bestandstype valideren
                if (contentType == null || (!contentType.contains("audio") && !contentType.contains("video"))) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Dit bestandstype is niet toegestaan! Alleen audio en video bestanden kunnen geüpload worden.");
                    return "redirect:/profile/upload/" + username;
                }

                // bestand opschonen en uniek maken
                String cleanName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.]", "_") + "_"
                        + System.currentTimeMillis();
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                Path path = Paths.get(UPLOAD_DIR + cleanName);
                Files.write(path, file.getBytes());
                String dbPath = "/uploads/" + cleanName;

                // Maak objecten aan en sla op in database
                // Maak de moeder
                Profile_File mainFile = new Profile_File();
                mainFile.setUser(user);
                mainFile.setBeschrijving(description);
                mainFile.setUploadDate(LocalDateTime.now());

                if (contentType.contains("audio")) {
                    mainFile.setFileType(FileType.AUDIO);

                    // Maak kind (audio)
                    Profile_Audio audio = new Profile_Audio();
                    audio.setAudioBestandPath(dbPath);

                    // Koppeling moeder-kind
                    audio.setProfileFile(mainFile);
                    mainFile.setProfileAudio(audio);

                } else if (contentType.contains("video")) {
                    mainFile.setFileType(FileType.VIDEO);

                    // Maak kind (video)
                    Profile_Video video = new Profile_Video();
                    video.setVideoBestandPath(dbPath);

                    // Koppeling moeder-kind
                    video.setProfileFile(mainFile);
                    mainFile.setProfileVideo(video);
                }

                // 4. Opslaan
                profileFileRepository.save(mainFile);
            }

            // Link opslaan (Youtube/Soundcloud)
            else if (!isLinkEmpty) {
                if (!link.contains("youtube.com") &&
                        !link.contains("youtu.be") &&
                        !link.contains("soundcloud.com")) {

                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Ongeldige link. Alleen YouTube en SoundCloud zijn toegestaan.");
                    return "redirect:/profile/upload/" + username;
                }

                // Maak objecten aan en sla op in database
                // Maak de moeder
                Profile_File mainFile = new Profile_File();
                mainFile.setUser(user);
                mainFile.setBeschrijving(description);
                mainFile.setUploadDate(LocalDateTime.now());
                mainFile.setFileType(FileType.LINK);

                // 2. Maak kind
                Profile_Link profileLink = new Profile_Link();
                profileLink.setUrl(link);

                // 3. Koppeling moeder-kind
                profileLink.setProfileFile(mainFile);
                mainFile.setProfileLink(profileLink);
                // 4. Opslaan (alleen moeder opslaan vanwege cascade)
                profileFileRepository.save(mainFile);
            }

            redirectAttributes.addFlashAttribute("successMessage", "\"" + description + "\" is succesvol geupload!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Fout bij uploaden.");
        }

        return "redirect:/profile/" + username;
    }

    @PostMapping("/profile/delete/{id}")
    public String deleteItem(@PathVariable Long id,
            @RequestParam("username") String username,
            RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/profile/" + username;

        // Haal bestand op
        Profile_File item = profileFileRepository.findById(id).orElse(null);

        if (item == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Item niet gevonden.");
            return redirectUrl;
        }

        String itemTitel = item.getBeschrijving();

        // Validatie: alleen eigenaar mag verwijderen
        if (!item.getUser().getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Je hebt geen rechten om \"" + itemTitel + "\" te verwijderen.");
            return redirectUrl;
        }

        try {
            // Fysieke bestanden verwijderen
            String filePath = null;
            if (item.isAudio()) {
                filePath = item.getProfileAudio().getAudioBestandPath();
            } else if (item.isVideo()) {
                filePath = item.getProfileVideo().getVideoBestandPath();
            }

            if (filePath != null) {
                File file = new File(System.getProperty("user.dir") + filePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            // Database record verwijderen
            profileFileRepository.delete(item);

            redirectAttributes.addFlashAttribute("successMessage", "\"" + itemTitel + "\" is succesvol verwijderd!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Er is iets fout gegaan bij het verwijderen van \"" + itemTitel + "\".");
        }

        return redirectUrl;
    }
}