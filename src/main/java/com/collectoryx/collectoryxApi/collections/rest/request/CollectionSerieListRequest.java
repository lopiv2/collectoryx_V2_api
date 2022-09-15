package com.collectoryx.collectoryxApi.collections.rest.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionSerieListRequest {

  @NotEmpty
  protected Long id;

  @NotEmpty
  protected String name;

  @NotEmpty
  protected Long collection;

  @NotEmpty
  protected String path;


}
