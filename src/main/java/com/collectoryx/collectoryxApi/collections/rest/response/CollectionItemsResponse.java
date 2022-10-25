package com.collectoryx.collectoryxApi.collections.rest.response;

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

  protected Long id;

  protected String name;

  protected ImageResponse image;

  protected CollectionListResponse collection;

  protected CollectionSeriesListResponse serie;

  protected Integer year;

  protected Float price;

  protected boolean own;

  protected boolean wanted;

  protected String notes;

  private Date acquiringDate;

  private List<CollectionItemMetadataResponse> metadata;

}
