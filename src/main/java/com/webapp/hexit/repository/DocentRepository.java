package com.webapp.hexit.repository;

import com.webapp.hexit.model.Docent;
import com.webapp.hexit.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocentRepository extends JpaRepository<Docent, Long> {
  Optional<Docent> findByUser(User user);
}
