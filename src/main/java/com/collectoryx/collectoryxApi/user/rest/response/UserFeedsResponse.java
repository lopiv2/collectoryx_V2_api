package com.collectoryx.collectoryxApi.user.rest.response;

import com.collectoryx.collectoryxApi.user.misc.UserFeedsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedsResponse {

  private Long id;

  private String name;

  private String rssUrl;

  private String logo;

  //Datos del feed
  private UserFeedsData feedData;

}
