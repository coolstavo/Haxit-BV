package com.webapp.hexit.repository;

import com.webapp.hexit.model.Lesson;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
  List<Lesson> findByDocentId(Long docentId);
}
