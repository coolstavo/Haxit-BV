package com.webapp.hexit.repository;

import com.webapp.hexit.model.Jam;
import com.webapp.hexit.model.Jam_Like;
import com.webapp.hexit.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JamLikeRepository extends JpaRepository<Jam_Like, Long> {
  List<Jam_Like> findByJamId(Long jamId);

  Optional<Jam_Like> findByUserAndJam(User user, Jam jam);

  long countByJamId(Long jamId);

  boolean existsByUserAndJam(User user, Jam jam);

  void deleteByUserAndJam(User user, Jam jam);
}
