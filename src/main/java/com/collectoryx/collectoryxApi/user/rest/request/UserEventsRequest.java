package com.collectoryx.collectoryxApi.user.rest.request;

import com.collectoryx.collectoryxApi.user.model.EventTypes;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class UserEventsRequest {

  @NotNull
  protected Long userId;

  @NotEmpty
  protected String title;

  protected String description;

  protected Date start;

  protected Date end;

  @Enumerated(EnumType.STRING)
  private EventTypes type;

}
