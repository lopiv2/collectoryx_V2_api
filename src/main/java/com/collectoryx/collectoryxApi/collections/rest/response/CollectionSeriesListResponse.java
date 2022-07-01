package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionSeriesListResponse {

  private Long id;

  private String name;

  private CollectionResponse collection;

  private ImageResponse logo;

}
