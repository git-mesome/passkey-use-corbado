package io.wisoft.public_auth.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  @Query("""
          SELECT c.address
          FROM User u JOIN u.company c
          WHERE u.email = :email
          """)
  List<String> findCompanyDomainsByUserEmail(@Param("email") String email);


}