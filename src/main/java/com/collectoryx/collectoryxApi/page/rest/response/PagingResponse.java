package com.collectoryx.collectoryxApi.page.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse<T> {

  @Schema(description = "List containing the items")
  private List<T> content;

  @Schema(description = "Page number of the response", example = "1")
  private long pageNumber;

  @Schema(description = "Number of elements in the response", example = "30")
  private int pageSize;

  @Schema(description = "Total pages", example = "3")
  private int totalPages;

  @Schema(description = "Total elements", example = "75")
  private long totalElements;

  @Schema(description = "Indicates if the page is the last", example = "false")
  private boolean last;

}
