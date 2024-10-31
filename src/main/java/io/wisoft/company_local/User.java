package io.wisoft.company_local;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User {

  @Id
  @Column(name = "email")
  private String email;

  @Column(name = "name")
  private String name;

}
