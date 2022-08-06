package com.collectoryx.collectoryxApi.user.misc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedsData {

  //Numero de articulos del feed
  private Integer articles;

  private String imageLink;

}
