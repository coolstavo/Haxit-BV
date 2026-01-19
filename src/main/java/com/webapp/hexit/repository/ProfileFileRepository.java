package com.webapp.hexit.repository;

import com.webapp.hexit.model.Profile_File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfileFileRepository extends JpaRepository<Profile_File, Long> {

    // Geef alle bestanden van een user gesorteerd op datum (nieuwste eerst)
    List<Profile_File> findByUser_UsernameOrderByUploadDateDesc(String username);
}
