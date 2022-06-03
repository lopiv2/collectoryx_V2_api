package com.collectoryx.collectoryxApi.config.rest.response;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {

  private Long id;

  private String name;

  private String url;

  private Date created;

}