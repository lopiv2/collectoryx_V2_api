package com.collectoryx.collectoryxApi.collections.rest.request;

import com.collectoryx.collectoryxApi.collections.model.CollectionMetadata;
import com.collectoryx.collectoryxApi.collections.model.CollectionTemplateType;
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
public class CollectionRequest {

  @NotEmpty
  protected Long id;

  @NotEmpty
  protected String name;

  @NotEmpty
  protected String file;

  @NotEmpty
  protected Boolean ambit;

  protected List<CollectionMetadata> metadata;

  protected CollectionTemplateType template;

  protected Long userId;


}
