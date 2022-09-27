package com.collectoryx.collectoryxApi.user.rest.response;

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
public class UserEventsResponse {

  @NotNull
  protected Long id;

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
