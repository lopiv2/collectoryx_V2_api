package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.collections.model.CollectionTemplateType;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionResponse {

  private Long id;

  private String name;

  private Integer owned;

  private Integer wanted;

  private Integer totalItems;

  private float totalPrice;

  private String collection;

  protected Boolean ambit;

  private CollectionTemplateType template;

  private ImageResponse logo;

}
