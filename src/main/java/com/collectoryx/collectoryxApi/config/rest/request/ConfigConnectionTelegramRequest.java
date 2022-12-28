package com.collectoryx.collectoryxApi.config.rest.request;

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
public class ConfigConnectionTelegramRequest {

  @NotNull
  private Long id;
  @NotNull
  private String name;

  private String botToken;

  private String chatId;

  private boolean sentNotifications;

}
