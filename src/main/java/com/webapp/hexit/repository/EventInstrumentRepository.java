package com.webapp.hexit.repository;

import com.webapp.hexit.model.EventInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventInstrumentRepository extends JpaRepository<EventInstrument, Long> {
    List<EventInstrument> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}
