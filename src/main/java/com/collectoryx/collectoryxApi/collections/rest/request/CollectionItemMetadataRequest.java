package com.collectoryx.collectoryxApi.collections.rest.request;

import com.collectoryx.collectoryxApi.collections.model.CollectionMetadataType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionItemMetadataRequest {

  private String id;

  private String name;

  @Enumerated(EnumType.STRING)
  private CollectionMetadataType type = CollectionMetadataType.STRING;

  @Schema(description = "Metadata value", example = "Foo")
  private String value;

}
