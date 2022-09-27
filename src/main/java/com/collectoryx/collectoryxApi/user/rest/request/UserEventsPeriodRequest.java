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
public class UserEventsPeriodRequest {

  @NotNull
  protected Long userId;

  @NotEmpty
  protected Long month;

  @NotEmpty
  protected Long year;

}
