package com.webapp.hexit.repository;

import com.webapp.hexit.model.Jam_Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JamCommentRepository extends JpaRepository<Jam_Comment, Long> {
  List<Jam_Comment> findByJamIdOrderByCreatedAtDesc(Long jamId);

  long countByJamId(Long jamId);
}
