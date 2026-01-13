package com.webapp.hexit.repository;

import com.webapp.hexit.model.Muzikant;
import com.webapp.hexit.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuzikantRepository extends JpaRepository<Muzikant, Long> {
    Optional<Muzikant> findByUser(User user);
}
