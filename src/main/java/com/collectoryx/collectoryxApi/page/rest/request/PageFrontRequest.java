package com.collectoryx.collectoryxApi.page.rest.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageFrontRequest {

  @NotEmpty
  protected Integer id;

  @NotEmpty
  protected Integer page;

  @NotEmpty
  protected Integer size;

  @NotEmpty
  protected String search;

  @NotEmpty
  protected String orderField;

  @NotEmpty
  protected String orderDirection;

}
