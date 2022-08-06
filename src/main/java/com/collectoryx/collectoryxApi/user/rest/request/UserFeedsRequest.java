package com.collectoryx.collectoryxApi.user.rest.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedsRequest {

  @NotNull
  protected Long userId;

  @NotEmpty
  protected String name;

  @NotEmpty
  protected String url;

}
