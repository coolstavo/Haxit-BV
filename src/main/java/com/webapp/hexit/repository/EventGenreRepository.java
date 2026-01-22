package com.webapp.hexit.repository;

import com.webapp.hexit.model.EventGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventGenreRepository extends JpaRepository<EventGenre, Long> {
    List<EventGenre> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}
