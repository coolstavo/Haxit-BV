package com.webapp.hexit.repository;

import com.webapp.hexit.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {}
