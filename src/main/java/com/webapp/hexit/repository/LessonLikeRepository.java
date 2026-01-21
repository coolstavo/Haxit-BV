package com.webapp.hexit.repository;

import com.webapp.hexit.model.Lesson;
import com.webapp.hexit.model.Lesson_Like;
import com.webapp.hexit.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonLikeRepository extends JpaRepository<Lesson_Like, Long> {
  List<Lesson_Like> findByLessonId(Long lessonId);

  Optional<Lesson_Like> findByUserAndLesson(User user, Lesson lesson);

  long countByLessonId(Long lessonId);

  boolean existsByUserAndLesson(User user, Lesson lesson);

  void deleteByUserAndLesson(User user, Lesson lesson);
}
