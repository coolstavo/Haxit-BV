package com.webapp.hexit.repository;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  List<Event> findByTitleContainingIgnoreCase(String title);
  List<Event> findByLatBetweenAndLngBetween(
    double latStart,
    double latEnd,
    double lngStart,
    double lngEnd
  );
  List<Event> findByCompanyUser(User companyUser);
  List<Event> findByCompanyUserId(Long companyUserId);
}
