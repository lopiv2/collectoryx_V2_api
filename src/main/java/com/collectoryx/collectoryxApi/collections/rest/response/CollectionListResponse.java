package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.collections.model.CollectionTemplateType;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import java.util.List;
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

  private Boolean ambit;

  private ImageResponse logo;

  private List<CollectionMetadataResponse> metadata;

  private CollectionTemplateType template;

  private Integer owned;

  private Integer wanted;

  private Integer totalItems;

  private float totalPrice;

}
