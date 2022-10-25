package com.collectoryx.collectoryxApi.collections.rest.request;

import com.collectoryx.collectoryxApi.collections.rest.response.CollectionMetadataResponse;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionCreateItemImportApiRequest {

  protected Long id;

  @NotEmpty
  protected String name;

  protected String image;

  protected Long collection;

  protected String serie;

  @NotEmpty
  protected Integer year;

  @NotEmpty
  protected Float price;

  @NotEmpty
  protected boolean own;

  protected boolean wanted;

  protected String notes;


  private Date acquiringDate;

  private List<CollectionMetadataResponse> metadata;

}
