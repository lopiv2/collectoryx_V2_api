package com.collectoryx.collectoryxApi.user.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Data
//@Table(name = "users")
@Document(collection = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String userName;

  //@Column(nullable = false, unique = true, length = 45)
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  //@Column(nullable = false, length = 64)
  @NotBlank
  @Size(max = 120)
  private String password;

  private String role;

}
