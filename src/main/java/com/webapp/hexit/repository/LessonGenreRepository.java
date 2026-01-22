package com.webapp.hexit.repository;

import com.webapp.hexit.model.LessonGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonGenreRepository extends JpaRepository<LessonGenre, Long> {
    List<LessonGenre> findByLessonId(Long lessonId);
    void deleteByLessonId(Long lessonId);
}
