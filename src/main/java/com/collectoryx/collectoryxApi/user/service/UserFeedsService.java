package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.user.model.UserFeeds;
import com.collectoryx.collectoryxApi.user.repository.UserFeedsRepository;
import com.collectoryx.collectoryxApi.user.rest.request.UserFeedsRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsResponse;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserFeedsService {

  private final UserFeedsRepository userFeedsRepository;

  public UserFeedsService(UserFeedsRepository userFeedsRepository) {
    this.userFeedsRepository = userFeedsRepository;
  }

  public void feedReader(List<String> args) {
    boolean ok = false;
    if (args.size() == 1) {
      try {
        URL feedUrl = new URL(args.get(0));

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

        System.out.println(feed);

        ok = true;
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("ERROR: " + ex.getMessage());
      }
    }

    if (!ok) {
      System.out.println();
      System.out.println("FeedReader reads and prints any RSS/Atom feed type.");
      System.out.println("The first parameter must be the URL of the feed to read.");
      System.out.println();
    }
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

  public UserFeedsResponse createFeed(UserFeedsRequest request)
      throws NotFoundException {
    UserFeedsResponse userFeedsResponse = null;
    UserFeeds userFeeds = UserFeeds.builder()
        .name(request.getName())
        .rssUrl(request.getUrl())
        .build();
    this.userFeedsRepository.save(userFeeds);
    userFeedsResponse = toUserFeedsResponseResponse(userFeeds);
    return userFeedsResponse;
  }


}
