package com.webapp.hexit.repository;

import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JamRepository extends JpaRepository<Jam, Long> {
  List<Jam> findByMuzikantUser(User muzikantUser);
  List<Jam> findByMuzikantUserId(Long muzikantUserId);
  List<Jam> findByTitleContainingIgnoreCase(String title);
}
