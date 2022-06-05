package com.collectoryx.collectoryxApi.collections.rest.request;

import com.collectoryx.collectoryxApi.collections.model.CollectionTypes;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRequest {

  @NotEmpty
  protected String name;

  @NotEmpty
  protected CollectionTypes template;

}
