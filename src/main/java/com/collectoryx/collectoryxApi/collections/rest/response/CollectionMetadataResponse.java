package com.collectoryx.collectoryxApi.collections.rest.response;

import com.collectoryx.collectoryxApi.collections.model.CollectionMetadataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionMetadataResponse {

  private String id;

  @Schema(description = "Metadata name", example = "Tag")
  private String name;

  @Schema(description = "Metadata type", example = "INTEGER")
  private CollectionMetadataType type;

  @Schema(description = "Metadata value", example = "Foo")
  private String value;

}
