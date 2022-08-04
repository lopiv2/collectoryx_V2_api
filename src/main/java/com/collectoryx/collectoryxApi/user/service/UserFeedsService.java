package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.user.model.UserFeeds;
import com.collectoryx.collectoryxApi.user.repository.UserFeedsRepository;
import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserFeedsService {

  private final UserFeedsRepository userFeedsRepository;

  public UserFeedsService(UserFeedsRepository userFeedsRepository) {
    this.userFeedsRepository = userFeedsRepository;
  }

  public List<UserFeedsResponse> listAllUserFeeds(Long id) {
    List<UserFeeds> userFeeds = this.userFeedsRepository
        .findAllByUserId(id);
    return StreamSupport.stream(userFeeds.spliterator(), false)
        .map(this::toUserFeedsResponseResponse)
        .collect(Collectors.toList());
  }

  private UserFeedsResponse toUserFeedsResponseResponse(UserFeeds request) {
    return UserFeedsResponse.builder()
        .name(request.getName())
        .rssUrl(request.getRssUrl())
        .build();
  }


}
