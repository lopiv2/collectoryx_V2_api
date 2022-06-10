package com.collectoryx.collectoryxApi.collections.rest.response;

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

  @Schema(description = "Metadata name", example = "Tag")
  private String name;

  @Schema(description = "Metadata value", example = "Foo")
  private String value;

}
