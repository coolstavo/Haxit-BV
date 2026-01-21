package com.webapp.hexit.repository;

import com.webapp.hexit.model.Lesson_Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonCommentRepository extends JpaRepository<Lesson_Comment, Long> {
  List<Lesson_Comment> findByLessonIdOrderByCreatedAtDesc(Long lessonId);

  long countByLessonId(Long lessonId);
}
