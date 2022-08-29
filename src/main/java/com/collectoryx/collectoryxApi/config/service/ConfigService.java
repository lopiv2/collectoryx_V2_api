package com.collectoryx.collectoryxApi.config.service;

import com.collectoryx.collectoryxApi.config.model.Config;
import com.collectoryx.collectoryxApi.config.repository.ConfigRepository;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigRequest;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigResponse;
import com.collectoryx.collectoryxApi.user.model.Themes;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.repository.UserThemesRepository;
import com.collectoryx.collectoryxApi.user.rest.request.ThemeRequest;
import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import com.collectoryx.collectoryxApi.user.rest.response.UserResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ConfigService {

  private final UserThemesRepository userThemesRepository;
  private final ConfigRepository configRepository;
  private final UserRepository userRepository;
  private final AdminService adminService;

  public ConfigService(UserThemesRepository userThemesRepository,
      ConfigRepository configRepository, UserRepository userRepository, AdminService adminService) {
    this.userThemesRepository = userThemesRepository;
    this.configRepository = configRepository;
    this.userRepository = userRepository;
    this.adminService = adminService;
  }

  public List<ThemeResponse> getAllThemes() {
    List<Themes> themes = this.userThemesRepository.findAll();
    return StreamSupport.stream(themes.spliterator(), false)
        .map(this::toThemeResponse)
        .collect(Collectors.toList());
  }

  public ConfigResponse getUserConfig(Long id) {
    Config config = this.configRepository.findByUser_Id(id);
    Themes themes = null;
    try {
      themes = this.userThemesRepository.findById(id)
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return toConfigResponse(config, themes);
  }

  public ConfigResponse saveConfig(ConfigRequest item) throws NotFoundException {
    if (item.getConfig().contains("appearance")) {
      Config config = this.configRepository.findByUser_Id(item.getId());
      config.setDarkTheme(item.isDark());
      User user = this.userRepository.findById(item.getId()).orElseThrow(NotFoundException::new);
      Themes themes = this.userThemesRepository.findById(item.getTheme())
          .orElseThrow(NotFoundException::new);
      user.setTheme(themes);
      this.configRepository.save(config);
      this.userRepository.save(user);
      return toConfigResponse(config, themes);
    } else {
      return null;
    }
  }

  public ThemeResponse saveTheme(ThemeRequest item) throws NotFoundException {
    User user = this.userRepository.findById(item.getUserId()).orElseThrow(NotFoundException::new);
    Themes theme = Themes.builder()
        .user(user)
        .name(item.getName())
        .backgroundColor(item.getBackgroundColor())
        .listItemColor(item.getListItemColor())
        .backgroundImage(item.getBackgroundImage())
        .mode(item.getMode())
        .primaryTextColor(item.getPrimaryTextColor())
        .secondaryTextColor(item.getSecondaryTextColor())
        .sideBarColor(item.getSideBarColor())
        .topBarColor(item.getTopBarColor())
        .build();
    this.userThemesRepository.save(theme);
    return toThemeResponse(theme);
  }

  private ConfigResponse toConfigResponse(Config request, Themes theme) {
    return ConfigResponse.builder()
        .id(request.getId())
        .theme(toThemeResponse(theme))
        .darkTheme(request.isDarkTheme())
        .build();
  }

  private ThemeResponse toThemeResponse(Themes request) {
    UserResponse userResponse = this.adminService.toUserResponse(request.getUser());
    return ThemeResponse.builder()
        .user(userResponse)
        .id(request.getId())
        .mode(request.getMode())
        .name(request.getName())
        .backgroundColor(request.getBackgroundColor())
        .backgroundImage(request.getBackgroundImage())
        .listItemColor(request.getListItemColor())
        .topBarColor(request.getTopBarColor())
        .sideBarColor(request.getSideBarColor())
        .secondaryTextColor(request.getSecondaryTextColor())
        .primaryTextColor(request.getPrimaryTextColor())
        .build();
  }

}
