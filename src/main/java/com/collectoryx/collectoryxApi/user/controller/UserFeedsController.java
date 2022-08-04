package com.collectoryx.collectoryxApi.user.controller;

import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsResponse;
import com.collectoryx.collectoryxApi.user.service.UserFeedsService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feeds")
@CrossOrigin
public class UserFeedsController {

  private final UserFeedsService userFeedsService;

  public UserFeedsController(UserFeedsService userFeedsService) {
    this.userFeedsService = userFeedsService;
  }

  @GetMapping(value = "/view/{id}")
  public Mono<List<UserFeedsResponse>> getUserFeeds(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<UserFeedsResponse> userFeedsResponses =
        this.userFeedsService.listAllUserFeeds(id);
    return Mono.just(userFeedsResponses);
  }

}
