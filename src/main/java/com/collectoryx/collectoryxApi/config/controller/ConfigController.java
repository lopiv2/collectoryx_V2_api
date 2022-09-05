package com.collectoryx.collectoryxApi.config.controller;

import com.collectoryx.collectoryxApi.config.rest.request.ConfigRequest;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigApiResponse;
import com.collectoryx.collectoryxApi.config.rest.response.ConfigResponse;
import com.collectoryx.collectoryxApi.config.service.ConfigService;
import com.collectoryx.collectoryxApi.user.rest.request.ThemeRequest;
import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping(value = "/create-theme")
  public Mono<ThemeResponse> createTheme(
      @RequestBody ThemeRequest themeRequest,
      @RequestHeader(value = "Authorization") String token) {
    ThemeResponse themeResponse = null;
    if (themeRequest.getName() != null) {
      try {
        themeResponse = this.configService.saveTheme(themeRequest);
      } catch (NotFoundException e) {
        e.printStackTrace();
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
        e.printStackTrace();
      }
    }
    return Mono.just(configResponse);
  }

}
