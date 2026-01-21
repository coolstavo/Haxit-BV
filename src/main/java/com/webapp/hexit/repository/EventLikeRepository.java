package com.webapp.hexit.repository;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.Event_Like;
import com.webapp.hexit.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventLikeRepository extends JpaRepository<Event_Like, Long> {
  List<Event_Like> findByEventId(Long eventId);

  Optional<Event_Like> findByUserAndEvent(User user, Event event);

  long countByEventId(Long eventId);

  boolean existsByUserAndEvent(User user, Event event);

  @Modifying
  @Transactional
  void deleteByUserAndEvent(User user, Event event);
}
