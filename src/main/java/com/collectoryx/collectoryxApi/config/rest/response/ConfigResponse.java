package com.collectoryx.collectoryxApi.config.rest.response;

import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigResponse {

  private Integer id;
  private ThemeResponse theme;
  private String latestVersion;
  private boolean expensivePanel;
  private boolean wishlistPanel;
  private boolean recentPurchasePanel;
  private boolean completedCollectionsPanel;
  private Boolean darkTheme;

}
