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

  protected int page;

  protected int rowsPerPage;

  protected String metadata;

  @NotEmpty
  protected String searchQuery;

  protected String header;

  protected String keyCode;

  @NotEmpty
  protected String url;

}
