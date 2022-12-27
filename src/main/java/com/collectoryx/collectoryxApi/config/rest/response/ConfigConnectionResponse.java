package com.collectoryx.collectoryxApi.config.rest.response;

import com.collectoryx.collectoryxApi.user.rest.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigConnectionResponse {

  private Long id;
  private String name;
  private boolean configured;
  private UserResponse user;

}
