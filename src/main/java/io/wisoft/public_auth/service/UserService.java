package io.wisoft.public_auth.service;

import io.wisoft.public_auth.persistance.Company;
import io.wisoft.public_auth.persistance.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private CompanyRepository companyRepository;

  public List<String> getCompanyDomainByEmail(String email) {
    return companyRepository.findCompanyDomainsByUserEmail(email);
  }

}
