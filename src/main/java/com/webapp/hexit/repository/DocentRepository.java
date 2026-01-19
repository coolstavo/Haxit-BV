package com.webapp.hexit.repository;

import com.webapp.hexit.model.Docent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocentRepository extends JpaRepository<Docent, Long> {
  Optional<Docent> findByNaam(String naam);
}
