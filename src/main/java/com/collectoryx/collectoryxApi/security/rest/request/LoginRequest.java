package com.collectoryx.collectoryxApi.security.rest.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @Email
  private String user_name;

  @NotEmpty
  private String password;

}
