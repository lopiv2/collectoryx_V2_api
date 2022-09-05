package com.collectoryx.collectoryxApi.config.rest.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigApiRequest {

  @NotEmpty
  private String name;

  @NotEmpty
  private String keyCode;

}
