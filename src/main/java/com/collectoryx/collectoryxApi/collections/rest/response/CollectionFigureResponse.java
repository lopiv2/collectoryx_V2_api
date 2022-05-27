package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.collections.model.CollectionTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionFigureResponse {

  private String collection;

  private Enum<CollectionTypes> template;

  private String logo;



}
