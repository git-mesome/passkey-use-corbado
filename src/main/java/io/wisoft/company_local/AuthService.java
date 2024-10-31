package io.wisoft.company_local;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

  private final UserRepository userRepository;

  @Autowired
  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public Optional<User> authenticateUser(String username, String email) {
    return userRepository.findByNameAndEmail(username, email);
  }

  // 이메일로 사용자 찾기 메서드
  public Optional<User> getUserByEmail(String userEmail) {
    return userRepository.findByEmail(userEmail);
  }

}
