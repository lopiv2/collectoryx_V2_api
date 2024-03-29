package com.collectoryx.collectoryxApi.config.service;

import com.collectoryx.collectoryxApi.config.model.Config;
import com.collectoryx.collectoryxApi.config.model.ConfigApiKeys;
import com.collectoryx.collectoryxApi.config.model.Connections;
import com.collectoryx.collectoryxApi.config.model.ConnectionsTelegram;
import com.collectoryx.collectoryxApi.config.repository.ConfigApiKeysRepository;
import com.collectoryx.collectoryxApi.config.repository.ConfigConnectionTelegramRepository;
import com.collectoryx.collectoryxApi.config.repository.ConfigConnectionsRepository;
import com.collectoryx.collectoryxApi.config.repository.ConfigRepository;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigApiRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigConnectionRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigConnectionTelegramRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigRequest;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigApiResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigConnectionResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigConnectionTelegramResponse;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
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
  private final ConfigConnectionsRepository configConnectionsRepository;
  private final ConfigConnectionTelegramRepository configConnectionTelegramRepository;

  public ConfigService(UserThemesRepository userThemesRepository,
      ConfigRepository configRepository, UserRepository userRepository, AdminService adminService,
      ConfigApiKeysRepository configApiKeysRepository,
      ConfigConnectionsRepository configConnectionsRepository,
      ConfigConnectionTelegramRepository configConnectionTelegramRepository) {
    this.userThemesRepository = userThemesRepository;
    this.configRepository = configRepository;
    this.userRepository = userRepository;
    this.adminService = adminService;
    this.configApiKeysRepository = configApiKeysRepository;
    this.configConnectionsRepository = configConnectionsRepository;
    this.configConnectionTelegramRepository = configConnectionTelegramRepository;
  }

  public ConfigApiResponse createApi(ConfigApiRequest request) {
    ConfigApiResponse configApiResponse = null;
    User user = null;
    try {
      user = this.userRepository.findById(request.getUserId())
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    ConfigApiKeys configApiKeys = ConfigApiKeys.builder()
        .apiLink(request.getApiLink())
        .keyCode(request.getKeyCode())
        .name(request.getName())
        .header(request.getHeader())
        .logo(request.getLogo())
        .user(user)
        .build();
    this.configApiKeysRepository.save(configApiKeys);
    return toConfigApiResponse(configApiKeys);
  }

  public ConfigApiResponse updateApi(ConfigApiRequest request)
      throws NotFoundException {
    ConfigApiKeys configApiKeys = this.configApiKeysRepository.findById(request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setApiLink(request.getApiLink());
          item.setKeyCode(request.getKeyCode());
          item.setLogo(request.getLogo());
          return this.configApiKeysRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    return toConfigApiResponse(configApiKeys);
  }

  public ConfigConnectionResponse updateConnection(ConfigConnectionRequest request)
      throws NotFoundException {
    Connections connections = this.configConnectionsRepository.findById(
            request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setConfigured(request.isConfigured());
          return this.configConnectionsRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    return toConfigConnectionResponse(connections);
  }

  public ConfigConnectionTelegramResponse updateTelegramConnection(
      ConfigConnectionTelegramRequest request) throws NotFoundException {
    ConnectionsTelegram connectionsTelegram = this.configConnectionTelegramRepository.findById(
            request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setBotToken(request.getBotToken());
          item.setChatId(request.getChatId());
          item.setSentNotifications(request.isSentNotifications());
          return this.configConnectionTelegramRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    return toConfigConnectionTelegramResponse(connectionsTelegram);
  }

  public void createInitialThemes() throws NotFoundException {
    Themes theme = this.userThemesRepository.findByName("defaultLight")
        .orElse(theme = Themes.builder()
            .name("defaultLight")
            .backgroundColor("")
            .listItemColor("#000000")
            .backgroundImage("")
            .mode("light")
            .primaryTextColor("#000000")
            .secondaryTextColor("rgba(0,0,0,0.6)")
            .sideBarColor("#fff")
            .topBarColor("#1976d2")
            .build());
    this.userThemesRepository.save(theme);
  }

  public void createInitialConfig(User user) {
    Themes theme = null;
    try {
      theme = this.userThemesRepository.findByName("defaultLight")
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    Config config = Config.builder()
        .darkTheme(true)
        .theme(theme)
        .expensiveItemPanel(true)
        .completedCollectionsPanel(true)
        .recentPurchasePanel(true)
        .wishlistPanel(true)
        .user(user)
        .build();
    this.configRepository.save(config);
  }

  public void checkUpdatedApis(Long userId) throws NotFoundException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
    InputStream input = getClass().getClassLoader().getResourceAsStream("initialApis.json");
    StringBuilder sb = new StringBuilder();
    User user1 = this.userRepository.findById(userId).orElseThrow(NotFoundException::new);
    try (InputStreamReader streamReader =
        new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      JSONArray jsonArray = new JSONArray(sb.toString());
      //System.out.println(jsonArray);
      try {
        ConfigApiKeys[] configApiKeysList = mapper.readValue(jsonArray.toString(),
            ConfigApiKeys[].class);
        for (ConfigApiKeys conf : configApiKeysList) {
          ConfigApiKeys configApiKeys = this.configApiKeysRepository.findByNameAndUser_Id(
              conf.getName(), userId);
          //If not config api found, create from zero
          if (configApiKeys == null) {
            ConfigApiKeys configApiKeys1 = ConfigApiKeys.builder()
                .user(user1)
                .name(conf.getName())
                .header(conf.getHeader())
                .keyCode(conf.getKeyCode())
                .apiLink(conf.getApiLink())
                .logo(conf.getLogo())
                .locked(conf.isLocked())
                .build();
            this.configApiKeysRepository.save(configApiKeys1);
          }
          //If found, updates it
          else {
            configApiKeys.setApiLink(conf.getApiLink());
            configApiKeys.setLogo(conf.getLogo());
            configApiKeys.setHeader(conf.getHeader());
            configApiKeys.setLocked(conf.isLocked());
            this.configApiKeysRepository.save(configApiKeys);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void checkUpdatedConnections(Long userId) throws NotFoundException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
    InputStream input = getClass().getClassLoader().getResourceAsStream("initialConnections.json");
    StringBuilder sb = new StringBuilder();
    User user1 = this.userRepository.findById(userId).orElseThrow(NotFoundException::new);
    try (InputStreamReader streamReader =
        new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      JSONArray jsonArray = new JSONArray(sb.toString());
      //System.out.println(jsonArray);
      try {
        Connections[] connections = mapper.readValue(jsonArray.toString(),
            Connections[].class);
        for (Connections conf : connections) {
          Connections con = this.configConnectionsRepository.findByNameAndUser_Id(
              conf.getName(), userId);
          //If not config connection found, create from zero
          if (con == null) {
            Connections c = Connections.builder()
                .user(user1)
                .name(conf.getName())
                .configured(conf.isConfigured())
                .build();
            this.configConnectionsRepository.save(c);
            if (conf.getName().contains("Telegram")) {
              //Telegram
              ConnectionsTelegram connectionsTelegram = ConnectionsTelegram.builder()
                  .connection(c)
                  .name(c.getName())
                  .sentNotifications(false)
                  .build();
              this.configConnectionTelegramRepository.save(connectionsTelegram);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void createInitialApiList(User user) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
    InputStream input = getClass().getClassLoader().getResourceAsStream("initialApis.json");
    StringBuilder sb = new StringBuilder();
    try (InputStreamReader streamReader =
        new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      JSONArray jsonArray = new JSONArray(sb.toString());
      //System.out.println(jsonArray);
      try {
        ConfigApiKeys[] configApiKeysList = mapper.readValue(jsonArray.toString(),
            ConfigApiKeys[].class);
        for (ConfigApiKeys conf : configApiKeysList) {
          ConfigApiKeys configApiKeys = ConfigApiKeys.builder()
              .user(user)
              .name(conf.getName())
              .header(conf.getHeader())
              .keyCode(conf.getKeyCode())
              .apiLink(conf.getApiLink())
              .logo(conf.getLogo())
              .build();
          this.configApiKeysRepository.save(configApiKeys);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void createInitialConnectionsList(User user) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
    InputStream input = getClass().getClassLoader().getResourceAsStream("initialConnections.json");
    StringBuilder sb = new StringBuilder();
    try (InputStreamReader streamReader =
        new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      JSONArray jsonArray = new JSONArray(sb.toString());
      //System.out.println(jsonArray);
      try {
        Connections[] connectionsList = mapper.readValue(jsonArray.toString(),
            Connections[].class);
        for (Connections conf : connectionsList) {
          Connections c = Connections.builder()
              .user(user)
              .name(conf.getName())
              .configured(conf.isConfigured())
              .build();
          this.configConnectionsRepository.save(c);
          if (conf.getName().contains("Telegram")) {
            //Telegram
            ConnectionsTelegram connectionsTelegram = ConnectionsTelegram.builder()
                .connection(c)
                .name(c.getName())
                .sentNotifications(false)
                .build();
            this.configConnectionTelegramRepository.save(connectionsTelegram);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
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
    //List<ConfigApiKeys> configApiKeysList = this.configApiKeysRepository.findAll();
    return StreamSupport.stream(configApiKeysList.spliterator(), false)
        .map(this::toConfigApiResponse)
        .collect(Collectors.toList());
  }

  public List<ConfigConnectionResponse> getAllConnectionsByUser(Long id) {
    List<Connections> connectionsList = this.configConnectionsRepository.findAllByUserId(id);
    return StreamSupport.stream(connectionsList.spliterator(), false)
        .map(this::toConfigConnectionResponse)
        .collect(Collectors.toList());
  }

  public List<ThemeResponse> getAllThemes() {
    List<Themes> themes = this.userThemesRepository.findAll();
    return StreamSupport.stream(themes.spliterator(), false)
        .map(this::toThemeResponse)
        .collect(Collectors.toList());
  }

  public String getLatestVersion() {
    String url = "https://github.com/lopiv2/collectoryx_v2_front";
    Element j = null;
    try {
      j = Jsoup.connect(url).get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Element version = j.select("table").first().select("tbody")
        .first().select("tr").first().select("td").get(2);
    int vPos = version.text().indexOf("currently");
    String latestVersion = version.text().substring(vPos + 10).replace(")", "");
    return latestVersion;
  }

  public ConfigResponse getUserConfig(Long id) {
    Config config = this.configRepository.findByUser_Id(id);
    String v = getLatestVersion();
    return toConfigResponse(config, v);
  }

  public ConfigConnectionTelegramResponse getTelegramConfig(Long id) {
    ConnectionsTelegram connectionsTelegram = this.configConnectionTelegramRepository
        .findByConnection_User_Id(id);
    return toConfigConnectionTelegramResponse(connectionsTelegram);
  }

  public ConfigResponse saveConfig(ConfigRequest item) throws NotFoundException {
    if (item.getConfig().contains("dashboard")) {
      Config config = this.configRepository.findByUser_Id(item.getId());
      config.setExpensiveItemPanel(item.isExpensivePanel());
      config.setWishlistPanel(item.isWishlistPanel());
      config.setCompletedCollectionsPanel(item.isCompletedCollectionsPanel());
      config.setRecentPurchasePanel(item.isRecentPurchasePanel());
      this.configRepository.save(config);
      return toConfigResponse(config);
    }
    if (item.getConfig().contains("appearance")) {
      Config config = this.configRepository.findByUser_Id(item.getId());
      config.setDarkTheme(item.isDark());
      //Si se modifica el tema
      Themes themes = null;
      if (item.getTheme() != null) {
        themes = this.userThemesRepository.findById(item.getTheme())
            .orElseThrow(NotFoundException::new);
        config.setTheme(themes);
      }
      this.configRepository.save(config);
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

  private ConfigConnectionTelegramResponse toConfigConnectionTelegramResponse(
      ConnectionsTelegram request) {
    return ConfigConnectionTelegramResponse.builder()
        .botToken(request.getBotToken())
        .id(request.getId())
        .chatId(request.getChatId())
        .name(request.getName())
        .connectionId(request.getConnection().getId())
        .sentNotifications(request.isSentNotifications())
        .build();
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
        .wishlistPanel(request.isWishlistPanel())
        .expensivePanel(request.isExpensiveItemPanel())
        .completedCollectionsPanel(request.isCompletedCollectionsPanel())
        .recentPurchasePanel(request.isRecentPurchasePanel())
        .darkTheme(request.isDarkTheme())
        .build();
  }

  private ConfigResponse toConfigResponse(Config request, String version) {
    return ConfigResponse.builder()
        .id(request.getId())
        .latestVersion(version)
        .wishlistPanel(request.isWishlistPanel())
        .expensivePanel(request.isExpensiveItemPanel())
        .completedCollectionsPanel(request.isCompletedCollectionsPanel())
        .recentPurchasePanel(request.isRecentPurchasePanel())
        .darkTheme(request.isDarkTheme())
        .build();
  }

  private ConfigApiResponse toConfigApiResponse(ConfigApiKeys request) {
    return ConfigApiResponse.builder()
        .id(request.getId())
        .name(request.getName())
        .header(request.getHeader())
        .logo(request.getLogo())
        .locked(request.isLocked())
        .apiLink(request.getApiLink())
        .keyCode(request.getKeyCode())
        .build();
  }

  private ConfigConnectionResponse toConfigConnectionResponse(Connections request) {
    UserResponse userResponse = this.adminService.toUserResponse(request.getUser());
    return ConfigConnectionResponse.builder()
        .id(request.getId())
        .name(request.getName())
        .configured(request.isConfigured())
        .user(userResponse)
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
