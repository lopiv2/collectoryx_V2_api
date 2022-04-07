package com.collectoryx.collectoryxApi.user.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  @GetMapping
  public Map<String, Object> getUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("username", authentication.getName());
    userMap.put("error", false);
    return userMap;
  }
}
