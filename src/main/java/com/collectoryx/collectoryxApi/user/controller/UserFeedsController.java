package com.collectoryx.collectoryxApi.user.controller;

import com.collectoryx.collectoryxApi.user.rest.request.UserFeedsRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsContentResponse;
import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsResponse;
import com.collectoryx.collectoryxApi.user.service.UserFeedsService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/feeds")
@CrossOrigin
public class UserFeedsController {

  private final UserFeedsService userFeedsService;

  public UserFeedsController(UserFeedsService userFeedsService) {
    this.userFeedsService = userFeedsService;
  }

  @GetMapping(value = "/get-all/{id}")
  public Mono<List<String>> readUserFeeds(
      @PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<UserFeedsResponse> userFeedsResponseList = this.userFeedsService.listAllUserFeeds(id);
    List<String> feeds = userFeedsResponseList.stream().map(x -> x.getRssUrl())
        .collect(Collectors.toList());
    List<String> userFeedReaderResponse = new ArrayList<>();
    userFeedReaderResponse.add(this.userFeedsService.feedReader(feeds).toString());
    return Mono.just(userFeedReaderResponse);
  }

  @GetMapping(value = "/get-feeds/{id}/{title}")
  public Mono<List<UserFeedsContentResponse>> readUserFeedsById(
      @PathVariable("id") Long id,
      @PathVariable("title") String title,
      @RequestHeader(value = "Authorization") String token) {
    UserFeedsResponse userFeedsResponseList = this.userFeedsService.getUserFeedsById(id, title);
    List<UserFeedsContentResponse> userFeedReaderResponse = new ArrayList<>();
    List<String> feeds = new ArrayList<>();
    feeds.add(userFeedsResponseList.getRssUrl());
    userFeedReaderResponse = this.userFeedsService.feedReader(feeds);
    return Mono.just(userFeedReaderResponse);
  }

  @GetMapping(value = "/view/{id}")
  public Mono<List<UserFeedsResponse>> getUserFeeds(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<UserFeedsResponse> userFeedsResponses =
        this.userFeedsService.listAllUserFeeds(id);
    return Mono.just(userFeedsResponses);
  }

  @PostMapping(value = "/create-feed")
  public Mono<UserFeedsResponse> createFeed(
      @RequestBody UserFeedsRequest userFeedsRequest,
      @RequestHeader(value = "Authorization") String token) {
    UserFeedsResponse userFeedsResponse = null;
    try {
      userFeedsResponse = this.userFeedsService.createFeed(userFeedsRequest);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return Mono.just(userFeedsResponse);
  }

}
