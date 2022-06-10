package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionItemsResponse {

  protected String name;

  protected ImageResponse image;

  protected String collection;

  protected CollectionSeriesList serie;

  protected Integer year;

  protected Float price;

  protected boolean own;

  protected String notes;

  private Date adquiringDate;

  private List<CollectionMetadataResponse> metadata;

}
