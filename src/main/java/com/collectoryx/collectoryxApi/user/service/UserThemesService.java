package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.user.model.Themes;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserThemesService {

  private final UserRepository userRepository;

  public UserThemesService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public ThemeResponse toThemesResponse(Themes request) {
    String backgroundImage = "";
    if (request.getBackgroundImage() == null) {
      backgroundImage = "";
    } else {
      backgroundImage = request.getBackgroundImage();
    }
    return ThemeResponse.builder()
        .id(request.getId())
        .backgroundImage(backgroundImage)
        .name(request.getName())
        .mode(request.getMode())
        .backgroundColor(request.getBackgroundColor())
        .listItemColor(request.getListItemColor())
        .primaryTextColor(request.getPrimaryTextColor())
        .secondaryTextColor(request.getSecondaryTextColor())
        .sideBarColor(request.getSideBarColor())
        .topBarColor(request.getTopBarColor())
        .build();
  }

}
