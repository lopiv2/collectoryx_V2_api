package com.collectoryx.collectoryxApi.image.rest.response;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {

  private Long id;

  private String name;

  private String path;

  private Date created;

}