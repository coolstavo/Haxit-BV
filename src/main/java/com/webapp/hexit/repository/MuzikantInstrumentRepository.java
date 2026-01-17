package com.webapp.hexit.repository;

import com.webapp.hexit.model.MuzikantInstrument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuzikantInstrumentRepository
    extends JpaRepository<MuzikantInstrument, Long>
{
    List<MuzikantInstrument> findByMuzikantId(Long muzikantId);
}
