package com.webapp.hexit.repository;

import com.webapp.hexit.model.Event_Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentRepository extends JpaRepository<Event_Comment, Long> {
  List<Event_Comment> findByEventIdOrderByCreatedAtDesc(Long eventId);

  long countByEventId(Long eventId);
}
