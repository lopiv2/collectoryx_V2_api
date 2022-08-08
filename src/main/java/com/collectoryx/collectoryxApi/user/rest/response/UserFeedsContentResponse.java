package com.collectoryx.collectoryxApi.user.rest.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedsContentResponse {

  private String title;

  private String description;

  private String link;

  private Date pubDate;

  private String image;

}
