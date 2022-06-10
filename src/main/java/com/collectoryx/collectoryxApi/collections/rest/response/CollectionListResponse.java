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
public class CollectionListResponse {

  private Long id;

  private String name;

  protected ImageResponse logo;

}
