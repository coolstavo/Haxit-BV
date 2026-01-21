package com.webapp.hexit.service;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.repository.EventRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private final EventRepository eventRepository;

  public SearchService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public List<Event> searchByTitle(String title) {
    return eventRepository.findByTitleContainingIgnoreCase(title);
  }

  public List<Event> searchByLocation(
    double latStart,
    double latEnd,
    double lngStart,
    double lngEnd
  ) {
    return eventRepository.findByLatBetweenAndLngBetween(
      latStart,
      latEnd,
      lngStart,
      lngEnd
    );
  }
}
