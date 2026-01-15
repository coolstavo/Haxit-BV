package com.webapp.hexit.repository;

import com.webapp.hexit.model.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    
    // Haalt een lijst van media-items op die gekoppeld zijn aan een gebruiker
    List<MediaItem> findByUsername(String username);
}