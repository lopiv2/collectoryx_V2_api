package com.collectoryx.collectoryxApi.config.controller;

import com.collectoryx.collectoryxApi.config.service.ConfigService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class ConfigController {

  private final ConfigService configService;

  public ConfigController(ConfigService configService) {
    this.configService = configService;
  }

}
