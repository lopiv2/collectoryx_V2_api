package com.collectoryx.collectoryxApi.config.service;

import com.collectoryx.collectoryxApi.config.model.Config;
import com.collectoryx.collectoryxApi.config.model.ConfigApiKeys;
import com.collectoryx.collectoryxApi.config.repository.ConfigApiKeysRepository;
import com.collectoryx.collectoryxApi.config.repository.ConfigRepository;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigApiRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigRequest;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigApiResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigResponse;
import com.collectoryx.collectoryxApi.user.model.Themes;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.repository.UserThemesRepository;
import com.collectoryx.collectoryxApi.user.rest.request.ThemeRequest;
import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import com.collectoryx.collectoryxApi.user.rest.response.UserResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
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
  private final ConfigApiKeysRepository configApiKeysRepository;

  public ConfigService(UserThemesRepository userThemesRepository,
      ConfigRepository configRepository, UserRepository userRepository, AdminService adminService,
      ConfigApiKeysRepository configApiKeysRepository) {
    this.userThemesRepository = userThemesRepository;
    this.configRepository = configRepository;
    this.userRepository = userRepository;
    this.adminService = adminService;
    this.configApiKeysRepository = configApiKeysRepository;
  }

  public ConfigApiResponse createApi(ConfigApiRequest request) {
    ConfigApiResponse configApiResponse = null;
    User user = null;
    try {
      user = this.userRepository.findById(request.getUserId())
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    ConfigApiKeys configApiKeys = ConfigApiKeys.builder()
        .apiLink(request.getApiLink())
        .keyCode(request.getKeyCode())
        .name(request.getName())
        .logo(request.getLogo())
        .user(user)
        .build();
    this.configApiKeysRepository.save(configApiKeys);
    return toConfigApiResponse(configApiKeys);
  }

  public void createInitialConfig(User user) {
    Themes theme = null;
    try {
      theme = this.userThemesRepository.findById(Long.valueOf(2))
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    Config config = Config.builder()
        .darkTheme(true)
        .theme(theme)
        .user(user)
        .build();
    this.configRepository.save(config);
  }

  public void createInitialApiList(User user) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
    File files = null;
    try {
      files = new File(System.getProperty("user.dir")).getCanonicalFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      ConfigApiKeys[] configApiKeys = mapper.readValue(
          new File(files + "/src/main/resources/initialApis.json"), ConfigApiKeys[].class);
      for (ConfigApiKeys conf : configApiKeys) {
        conf.setUser(user);
        this.configApiKeysRepository.save(conf);
        System.out.println(conf);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean deleteApi(Long id) throws NotFoundException {
    ConfigApiKeys col = this.configApiKeysRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.configApiKeysRepository.deleteById(Long.valueOf(col.getId()));
    return true;
  }

  public List<ConfigApiResponse> getAllApisByUser(Long id) {
    List<ConfigApiKeys> configApiKeysList = this.configApiKeysRepository.findAllByUserId(id);
    return StreamSupport.stream(configApiKeysList.spliterator(), false)
        .map(this::toConfigApiResponse)
        .collect(Collectors.toList());
  }

  public List<ThemeResponse> getAllThemes() {
    List<Themes> themes = this.userThemesRepository.findAll();
    return StreamSupport.stream(themes.spliterator(), false)
        .map(this::toThemeResponse)
        .collect(Collectors.toList());
  }

  public ConfigResponse getUserConfig(Long id) {
    Config config = this.configRepository.findByUser_Id(id);
    return toConfigResponse(config);
  }

  public ConfigResponse saveConfig(ConfigRequest item) throws NotFoundException {
    if (item.getConfig().contains("appearance")) {
      Config config = this.configRepository.findByUser_Id(item.getId());
      config.setDarkTheme(item.isDark());
      User user = this.userRepository.findById(item.getId()).orElseThrow(NotFoundException::new);
      Themes themes = this.userThemesRepository.findById(item.getTheme())
          .orElseThrow(NotFoundException::new);
      config.setTheme(themes);
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

  private ConfigResponse toConfigResponse(Config request) {
    return ConfigResponse.builder()
        .id(request.getId())
        .darkTheme(request.isDarkTheme())
        .build();
  }

  private ConfigApiResponse toConfigApiResponse(ConfigApiKeys request) {
    return ConfigApiResponse.builder()
        .id(request.getId())
        .name(request.getName())
        .logo(request.getLogo())
        .apiLink(request.getApiLink())
        .keyCode(request.getKeyCode())
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
