package com.collectoryx.collectoryxApi.user.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeResponse {

  private UserResponse user;

  private Long id;

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
