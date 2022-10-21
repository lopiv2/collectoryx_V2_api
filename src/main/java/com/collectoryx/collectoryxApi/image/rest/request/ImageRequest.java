package com.collectoryx.collectoryxApi.image.rest.request;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageRequest {

  protected Long id;

  protected String name;

  protected String path;

  protected Date created;

}