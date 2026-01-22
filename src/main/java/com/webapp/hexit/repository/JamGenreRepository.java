package com.webapp.hexit.repository;

import com.webapp.hexit.model.JamGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JamGenreRepository extends JpaRepository<JamGenre, Long> {
    List<JamGenre> findByJamId(Long jamId);
    void deleteByJamId(Long jamId);
}
