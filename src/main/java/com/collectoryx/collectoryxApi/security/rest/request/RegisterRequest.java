package com.collectoryx.collectoryxApi.security.rest.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


  private String userName;
  private String firstName;
  private String lastName;

  private String email;

  @NotEmpty
  private String password;

}
