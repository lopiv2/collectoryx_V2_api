package com.collectoryx.collectoryxApi.config.rest.request;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigConnectionRequest {

  @NotNull
  private Long id;
  @NotNull
  private String name;
  @NotEmpty
  private boolean configured;
  @NotEmpty
  private Long user;

}
