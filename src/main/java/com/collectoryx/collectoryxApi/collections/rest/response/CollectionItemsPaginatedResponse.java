package com.collectoryx.collectoryxApi.collections.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionItemsPaginatedResponse {

  protected int pages;

  protected int totalCount;

  protected List<CollectionItemsResponse> items;

}
