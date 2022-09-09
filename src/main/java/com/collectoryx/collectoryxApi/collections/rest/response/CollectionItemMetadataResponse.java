package com.collectoryx.collectoryxApi.collections.rest.response;

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
public class CollectionItemMetadataResponse {

  private Long id;

  @Schema(description = "Metadata name", example = "Title")
  private String name;

  @Enumerated(EnumType.STRING)
  private CollectionMetadataType type = CollectionMetadataType.STRING;

  @Schema(description = "Metadata value", example = "Foo")
  private String value;

}
