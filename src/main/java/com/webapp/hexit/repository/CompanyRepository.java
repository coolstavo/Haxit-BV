package com.webapp.hexit.repository;

import com.webapp.hexit.model.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  Optional<Company> findByCompanyName(String companyName);
}
