package com.collectoryx.collectoryxApi.util.rest.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrapperApiRequest {

  @NotEmpty
  protected String searchQuery;

  protected String header;

  protected String keyCode;

  @NotEmpty
  protected String url;

}