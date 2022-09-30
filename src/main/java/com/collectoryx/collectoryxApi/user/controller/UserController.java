package com.collectoryx.collectoryxApi.user.controller;

import com.collectoryx.collectoryxApi.user.rest.request.UserRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserResponse;
import com.collectoryx.collectoryxApi.user.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public Map<String, Object> getUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("username", authentication.getName());
    userMap.put("error", false);
    return userMap;
  }

  @GetMapping(value = "/profile/{id}")
  public Mono<UserResponse> getUserProfile(
      @PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    UserResponse userResponse= null;
    try {
      userResponse = this.userService.getUserDetails(id);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return Mono.just(userResponse);
  }

  @PutMapping(value = "/profile/update")
  public Mono<UserResponse> updateUserProfile(
      @RequestBody UserRequest userRequest,
      @RequestHeader(value = "Authorization") String token) {
    UserResponse userResponse = null;
    try {
      userResponse = this.userService.updateUserDetails(userRequest);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return Mono.just(userResponse);
  }
}
