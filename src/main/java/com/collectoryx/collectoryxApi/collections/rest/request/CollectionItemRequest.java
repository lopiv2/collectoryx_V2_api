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
public class CollectionItemRequest {

  @NotEmpty
  protected Long id;

  protected String name;

  protected Integer year;

  protected String serie;

  protected long collection;

  @NotEmpty
  protected Boolean own;

  @NotEmpty
  protected Boolean wanted;

}
