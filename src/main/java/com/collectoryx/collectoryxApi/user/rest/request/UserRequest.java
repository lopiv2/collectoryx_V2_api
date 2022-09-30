package com.collectoryx.collectoryxApi.user.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

  private Long id;

  private String email;

  private String firstName;

  private String lastName;

  private String password;

  private String userName;

}
