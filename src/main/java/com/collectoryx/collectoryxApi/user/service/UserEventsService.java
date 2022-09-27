package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserEvents;
import com.collectoryx.collectoryxApi.user.repository.UserEventsRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.request.UserEventsPeriodRequest;
import com.collectoryx.collectoryxApi.user.rest.request.UserEventsRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserEventsResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserEventsService {

  private final UserEventsRepository userEventsRepository;
  private final UserRepository userRepository;
  private final AdminService adminService;

  public UserEventsService(UserEventsRepository userEventsRepository, UserRepository userRepository,
      AdminService adminService) {
    this.userEventsRepository = userEventsRepository;
    this.userRepository = userRepository;
    this.adminService = adminService;
  }

  public UserEventsResponse createEvent(UserEventsRequest request)
      throws NotFoundException {
    User user = this.userRepository.findById(request.getUserId())
        .orElseThrow(NotFoundException::new);
    UserEvents userEvents = UserEvents.builder()
        .user(user)
        .title(request.getTitle())
        .type(request.getType())
        .end(request.getEnd())
        .start(request.getStart())
        .description(request.getDescription())
        .build();
    this.userEventsRepository.save(userEvents);
    UserEventsResponse userEventsResponse = toUserEventsResponse(userEvents);
    return userEventsResponse;
  }

  public boolean deleteEvent(Long id) throws NotFoundException {
    UserEvents userEvents = this.userEventsRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.userEventsRepository.deleteById(userEvents.getId());
    return true;
  }


  /*public UserFeedsResponse getUserFeedsById(Long id, String title) {
    UserFeeds userFeed = this.userFeedsRepository
        .findByUserIdAndName(id, title);
    return toUserFeedsResponse(userFeed);
  }*/

  public List<UserEventsResponse> listEventsByMonthAndYear(
      UserEventsPeriodRequest userEventsPeriodRequest) {
    List<UserEvents> userEvents = this.userEventsRepository.findByPeriod(
        userEventsPeriodRequest.getUserId(), userEventsPeriodRequest.getMonth(),
        userEventsPeriodRequest.getYear());
    return StreamSupport.stream(userEvents.spliterator(), false)
        .map(this::toUserEventsResponse)
        .collect(Collectors.toList());
  }

  /*public UserFeedsResponse updateFeed(UserFeedsRequest request)
      throws NotFoundException {
    UserFeeds userFeeds = this.userFeedsRepository
        .findById(request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setRssUrl(request.getUrl());
          return this.userFeedsRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    return toUserFeedsResponse(userFeeds);
  }*/

  private UserEventsResponse toUserEventsResponse(UserEvents request) {
    return UserEventsResponse.builder()
        .id(request.getId())
        .title(request.getTitle())
        .type(request.getType())
        .end(request.getEnd())
        .start(request.getStart())
        .description(request.getDescription())
        .build();
  }
}
