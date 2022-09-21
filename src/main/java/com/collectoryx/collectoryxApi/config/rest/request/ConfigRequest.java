package com.collectoryx.collectoryxApi.config.rest.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigRequest {

  @NotEmpty
  protected Long id;

  @NotEmpty
  protected Long theme;

  @NotEmpty
  protected boolean expensivePanel;

  @NotEmpty
  protected boolean wishlistPanel;

  @NotEmpty
  protected boolean recentPurchasePanel;

  @NotEmpty
  protected boolean completedCollectionsPanel;

  @NotEmpty
  protected boolean dark;

  @NotEmpty
  protected String config;

}
