package com.collectoryx.collectoryxApi.user.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeRequest {

  private Long userId;

  private String name;

  private String mode;

  private String topBarColor;

  private String primaryTextColor;

  private String secondaryTextColor;

  private String listItemColor;

  private String sideBarColor;

  private String backgroundImage="";

  private String backgroundColor;

}
