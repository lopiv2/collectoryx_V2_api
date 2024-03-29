package com.collectoryx.collectoryxApi.config.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigApiResponse {

  private Long id;
  private String name;
  private String keyCode;
  private String header;
  private String apiLink;
  private String logo;
  private boolean locked;

}
