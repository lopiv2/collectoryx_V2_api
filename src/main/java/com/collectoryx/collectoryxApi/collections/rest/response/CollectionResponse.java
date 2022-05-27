package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.collections.model.CollectionTypes;
import com.collectoryx.collectoryxApi.images.model.Images;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionResponse {

  private String collection;

  private Enum<CollectionTypes> template;

  private Images logo;

}
