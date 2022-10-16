package com.collectoryx.collectoryxApi.user.controller;

import com.collectoryx.collectoryxApi.user.rest.request.UserEventsRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserEventsResponse;
import com.collectoryx.collectoryxApi.user.service.UserEventsService;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/events")
@CrossOrigin
public class UserEventsController {

  private final UserEventsService userEventsService;

  public UserEventsController(UserEventsService userEventsService) {
    this.userEventsService = userEventsService;
  }

  @PostMapping(value = "/create-event")
  public Mono<UserEventsResponse> createEvent(
      @RequestBody UserEventsRequest userEventsRequest,
      @RequestHeader(value = "Authorization") String token) {
    UserEventsResponse userEventsResponse = null;
    try {
      userEventsResponse = this.userEventsService.createEvent(userEventsRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(userEventsResponse);
  }

  @DeleteMapping(value = "/delete-event/{id}")
  public Mono<Boolean> deleteFeed(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.userEventsService.deleteEvent(id);
    return Mono.just(isDeleted);
  }

  @PostMapping(value = "/get-period")
  public Mono<List<UserEventsResponse>> getPeriodEvents(
      @RequestBody UserEventsRequest userEventsPeriodRequest,
      @RequestHeader(value = "Authorization") String token) {
    List<UserEventsResponse> userEventsResponseList = this.userEventsService
        .listEventsByMonthAndYear(userEventsPeriodRequest);
    return Mono.just(userEventsResponseList);
  }

  @PutMapping(value = "/update")
  public Mono<UserEventsResponse> updateFeed(
      @RequestBody UserEventsRequest userEventsRequest,
      @RequestHeader(value = "Authorization") String token) {
    UserEventsResponse userEventsResponse = null;
    try {
      userEventsResponse = this.userEventsService.updateEvent(userEventsRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(userEventsResponse);
  }

  /*@GetMapping(value = "/view/{id}")
  public Mono<List<UserEventsResponse>> getUserFeeds(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<UserEventsResponse> userEventsResponses =
        this.userEventsService.listAllUserFeeds(id);
    return Mono.just(userEventsResponses);
  }*/
}
