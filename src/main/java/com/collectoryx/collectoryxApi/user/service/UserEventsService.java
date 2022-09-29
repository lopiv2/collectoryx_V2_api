package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserEvents;
import com.collectoryx.collectoryxApi.user.repository.UserEventsRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
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
        .allDay(request.isAllDay())
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


  public List<UserEventsResponse> listEventsByMonthAndYear(
      UserEventsRequest userEventsPeriodRequest) {
    List<UserEvents> userEvents = this.userEventsRepository.findByPeriod(
        userEventsPeriodRequest.getUserId(), userEventsPeriodRequest.getStart(),
        userEventsPeriodRequest.getEnd());
    return StreamSupport.stream(userEvents.spliterator(), false)
        .map(this::toUserEventsResponse)
        .collect(Collectors.toList());
  }

  public UserEventsResponse updateEvent(UserEventsRequest request)
      throws NotFoundException {
    UserEvents userEvents = this.userEventsRepository
        .findById(request.getId())
        .map(item -> {
          item.setDescription(request.getDescription());
          item.setStart(request.getStart());
          item.setTitle(request.getTitle());
          item.setType(request.getType());
          item.setEnd(request.getEnd());
          item.setAllDay(request.isAllDay());
          return this.userEventsRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    return toUserEventsResponse(userEvents);
  }

  private UserEventsResponse toUserEventsResponse(UserEvents request) {
    return UserEventsResponse.builder()
        .id(request.getId())
        .title(request.getTitle())
        .type(request.getType())
        .end(request.getEnd())
        .allDay(request.isAllDay())
        .start(request.getStart())
        .description(request.getDescription())
        .build();
  }
}
