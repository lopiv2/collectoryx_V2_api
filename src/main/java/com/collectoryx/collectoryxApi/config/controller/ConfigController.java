package com.collectoryx.collectoryxApi.config.controller;

import com.collectoryx.collectoryxApi.config.rest.request.ConfigApiRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigConnectionRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigConnectionTelegramRequest;
import com.collectoryx.collectoryxApi.config.rest.request.ConfigRequest;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigApiResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigConnectionResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigConnectionTelegramResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigResponse;
import com.collectoryx.collectoryxApi.config.service.ConfigService;
import com.collectoryx.collectoryxApi.user.rest.request.ThemeRequest;
import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/config")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class ConfigController {

  private final ConfigService configService;

  public ConfigController(ConfigService configService) {
    this.configService = configService;
  }

  @GetMapping(value = "/check-config-connection/{id}")
  public Mono<List<ConfigConnectionResponse>> checkConfigConnection(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<ConfigConnectionResponse> configConnectionResponses =
        this.configService.getAllConnectionsByUser(id);
    return Mono.just(configConnectionResponses);
  }

  @GetMapping(value = "/get-config-telegram/{id}")
  public Mono<ConfigConnectionTelegramResponse> checkConfigTelegram(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    ConfigConnectionTelegramResponse configConnectionTelegramResponse =
        this.configService.getTelegramConfig(id);
    return Mono.just(configConnectionTelegramResponse);
  }


  @PostMapping(value = "/create-theme")
  public Mono<ThemeResponse> createTheme(
      @RequestBody ThemeRequest themeRequest,
      @RequestHeader(value = "Authorization") String token) {
    ThemeResponse themeResponse = null;
    if (themeRequest.getName() != null) {
      try {
        themeResponse = this.configService.saveTheme(themeRequest);
      } catch (NotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return Mono.just(themeResponse);
  }

  @GetMapping(value = "/get-api-list/{id}")
  public Mono<List<ConfigApiResponse>> getAllApis(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<ConfigApiResponse> configApiResponseList =
        this.configService.getAllApisByUser(id);
    return Mono.just(configApiResponseList);
  }

  @PostMapping(value = "/create-api")
  public Mono<ConfigApiResponse> createApi(
      @RequestBody ConfigApiRequest configApiRequest,
      @RequestHeader(value = "Authorization") String token) {
    ConfigApiResponse configApiResponse = null;
    configApiResponse = this.configService.createApi(configApiRequest);
    return Mono.just(configApiResponse);
  }

  @PutMapping(value = "/update-api")
  public Mono<ConfigApiResponse> updateItem(
      @RequestBody ConfigApiRequest configApiRequest,
      @RequestHeader(value = "Authorization") String token) {
    ConfigApiResponse configApiResponse = null;
    try {
      configApiResponse = this.configService.updateApi(configApiRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(configApiResponse);
  }

  @PutMapping(value = "/update-connection")
  public Mono<ConfigConnectionResponse> updateConnectionConfig(
      @RequestBody ConfigConnectionRequest configConnectionRequest,
      @RequestHeader(value = "Authorization") String token) {
    ConfigConnectionResponse configConnectionResponse = null;
    try {
      configConnectionResponse = this.configService.updateConnection(
          configConnectionRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(configConnectionResponse);
  }

  @PutMapping(value = "/update-telegram")
  public Mono<ConfigConnectionTelegramResponse> updateTelegramConfig(
      @RequestBody ConfigConnectionTelegramRequest configConnectionTelegramRequest,
      @RequestHeader(value = "Authorization") String token) {
    ConfigConnectionTelegramResponse configConnectionTelegramResponse = null;
    try {
      configConnectionTelegramResponse = this.configService.updateTelegramConnection(
          configConnectionTelegramRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(configConnectionTelegramResponse);
  }

  @DeleteMapping(value = "/delete-api/{id}")
  public Mono<Boolean> deleteApi(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.configService.deleteApi(id);
    return Mono.just(isDeleted);
  }

  @GetMapping(value = "/get-config/{id}")
  public Mono<ConfigResponse> getUserConfig(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    ConfigResponse configResponseList =
        this.configService.getUserConfig(id);
    return Mono.just(configResponseList);
  }

  @GetMapping(value = "/get-themes")
  public Mono<List<ThemeResponse>> getAllThemes(
      @RequestHeader(value = "Authorization") String token) {
    List<ThemeResponse> themeResponses =
        this.configService.getAllThemes();
    return Mono.just(themeResponses);
  }

  @PostMapping(value = "/save")
  public Mono<ConfigResponse> saveConfig(
      @RequestBody ConfigRequest configRequest,
      @RequestHeader(value = "Authorization") String token) {
    ConfigResponse configResponse = null;
    if (configRequest.getId() != null) {
      try {
        configResponse = this.configService.saveConfig(configRequest);
      } catch (NotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return Mono.just(configResponse);
  }

}
