package com.collectoryx.collectoryxApi.config.rest.request;

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
public class ConfigApiRequest {

  protected Long id;

  @NotNull
  protected Long userId;

  @NotEmpty
  private String apiLink;

  @NotEmpty
  private String header;

  @NotEmpty
  private String name;

  @NotEmpty
  private String keyCode;


  private String logo;

  @NotEmpty
  private boolean locked; //Is a default API and can´t be deleted

}
