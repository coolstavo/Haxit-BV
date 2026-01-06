package com.webapp.hexit.repository;

import com.webapp.hexit.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  // no custom methods needed yet
}
