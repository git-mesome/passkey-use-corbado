package io.wisoft.public_auth.persistance;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;

@Getter
@Entity
@Table(name = "company")
public class Company {

  @Id
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @OneToMany(mappedBy = "company")
  private Set<User> users;

}
