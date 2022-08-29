package com.collectoryx.collectoryxApi.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/theme")
public class UserThemeController {

  /*@GetMapping
  public Map<String, Object> getUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("username", authentication.getName());
    userMap.put("error", false);
    return userMap;
  }*/
}
