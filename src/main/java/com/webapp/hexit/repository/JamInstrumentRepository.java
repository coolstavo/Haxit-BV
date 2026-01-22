package com.webapp.hexit.repository;

import com.webapp.hexit.model.JamInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JamInstrumentRepository extends JpaRepository<JamInstrument, Long> {
    List<JamInstrument> findByJamId(Long jamId);
    void deleteByJamId(Long jamId);
}
