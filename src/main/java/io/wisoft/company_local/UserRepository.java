package io.wisoft.company_local;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByNameAndEmail(String name, String email);

  Optional<User> findByEmail(String email);

}
