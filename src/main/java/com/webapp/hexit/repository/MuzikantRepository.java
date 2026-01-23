package com.webapp.hexit.repository;

import com.webapp.hexit.model.Genre;
import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MuzikantRepository extends JpaRepository<Muzikant, Long> {
    Optional<Muzikant> findByUser(User user);

    // Haal iedereen op die gedeelde genres heeft exclusief gebruiker zelf
    @Query("SELECT DISTINCT m FROM Muzikant m " +
           "JOIN m.genres g " +
           "WHERE g IN :genres " +
           "AND m.user.username != :currentUsername")
    List<Muzikant> findMatches(@Param("genres") List<Genre> genres, 
                               @Param("currentUsername") String currentUsername);
}
