package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.user.misc.UserFeedsData;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserFeeds;
import com.collectoryx.collectoryxApi.user.repository.UserFeedsRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.request.UserFeedsRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsContentResponse;
import com.collectoryx.collectoryxApi.user.rest.response.UserFeedsResponse;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class UserFeedsService {

  private final UserFeedsRepository userFeedsRepository;
  private final UserRepository userRepository;
  private final AdminService adminService;

  public UserFeedsService(UserFeedsRepository userFeedsRepository, UserRepository userRepository,
      AdminService adminService) {
    this.userFeedsRepository = userFeedsRepository;
    this.userRepository = userRepository;
    this.adminService = adminService;
  }

  public UserFeedsResponse createFeed(UserFeedsRequest request)
      throws NotFoundException {
    User user = this.userRepository.findById(request.getUserId())
        .orElseThrow(NotFoundException::new);
    String getLogo =
        "https://t3.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url="
            + request.getCleanUrl() + "?&size=128";
    UserFeeds userFeeds = UserFeeds.builder()
        .user(user)
        .name(request.getName())
        .rssUrl(request.getUrl())
        .logo(getLogo)
        .build();
    this.userFeedsRepository.save(userFeeds);
    UserFeedsResponse userFeedsResponse = toUserFeedsResponse(userFeeds);
    return userFeedsResponse;
  }

  public boolean deleteFeed(Long id) throws NotFoundException {
    UserFeeds col = this.userFeedsRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.userFeedsRepository.deleteById(col.getId());
    return true;
  }

  public List<UserFeedsContentResponse> feedReader(List<String> args) {
    List<UserFeedsContentResponse> userFeedsContentResponseList = new ArrayList<>();
    boolean ok = false;
    if (args.size() == 1) {
      org.jsoup.nodes.Document docs = null;
      try {
        docs = Jsoup.connect(args.get(0)).get();
      } catch (IOException e) {
        e.printStackTrace();
      }
      String feedUrls = docs.select("link").first().text();
      //Select item feed
      Elements e = docs.select("item");
      for (int x = 0; x < e.size(); x++) {
        String image = null;
        String title = e.get(x).select("title").text();
        String description = e.get(x).select("description").text();
        String link = e.get(x).select("link").text();
        String date = e.get(x).select("pubDate").text();
        DateFormat formatter = null;
        if (date.contains("GMT")) {
          formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
              Locale.US);
        } else {
          formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",
              Locale.US);
        }
        Date pubDate = null;
        try {
          pubDate = formatter.parse(date);
        } catch (ParseException ex) {
          ex.printStackTrace();
        }
        //Check for image
        org.jsoup.nodes.Element img = e.get(x).select("image").first();
        if (img != null) {
          image = img.text();
        } else {
          //Check for image inside media:content
          org.jsoup.nodes.Element imgM = e.get(x).select("media|content").first();
          if (imgM != null) {
            image = imgM.attr("url");
          } else {
            //Find image inside content:encoded
            org.jsoup.nodes.Element i = e.get(x).select("content|encoded").first();
            if (i != null) {
              org.jsoup.nodes.Document d = Jsoup.parseBodyFragment(i.text());
              String linkImg = d.select("img").first().attr("src");
              //Check if absolute or relative route for images
              if (linkImg.contains("http")) {
                image = linkImg;
              } else {
                image = feedUrls + linkImg;
              }
            }
            //find image inside description
            else {
              Element desc = e.get(x).select("description").first();
              //Parse because RSS is encrypted
              if (desc != null) {
                Document imd = Jsoup.parseBodyFragment(desc.text());
                image = imd.select("img").attr("src");
              }
              /*if (image == null || image == "") {
                //Check for image inside item link of feed
                Document im = null;
                try {
                  im = Jsoup.connect(link).userAgent(
                          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
                      .get();
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
                Element imContainer = im.getElementsByClass("entry-content").first();
                image = imContainer.select("img").first().attr("src")
                    .replaceAll("\\bhttp\\b", "https");

              }*/
            }
          }
        }
        UserFeedsContentResponse userFeedsContentResponse = UserFeedsContentResponse.builder()
            .title(title)
            .description(description)
            .link(link)
            .pubDate(pubDate)
            .image(image)
            .build();
        userFeedsContentResponseList.add(userFeedsContentResponse);
      }
      return userFeedsContentResponseList;
    }

    if (!ok) {
      System.out.println();
      System.out.println("FeedReader reads and prints any RSS/Atom feed type.");
      System.out.println("The first parameter must be the URL of the feed to read.");
      System.out.println();
    }
    return null;
  }

  //Number of entries per feed
  public UserFeedsData feedParserEntries(List<String> args) {
    boolean ok = false;
    if (args.size() == 1) {
      try {
        URL feedUrl = new URL(args.get(0));

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        List<SyndEntry> items = feed.getEntries();
        UserFeedsData userFeedsData = null;
        return userFeedsData.builder()
            .articles(items.size())
            .build();

        //return items.size();
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
    return null;
  }

  public UserFeedsResponse getUserFeedsById(Long id, String title) {
    UserFeeds userFeed = this.userFeedsRepository
        .findByUserIdAndName(id, title);
    return toUserFeedsResponse(userFeed);
  }

  public List<UserFeedsResponse> listAllUserFeeds(Long id) {
    List<UserFeeds> userFeeds = this.userFeedsRepository
        .findAllByUserId(id);
    return StreamSupport.stream(userFeeds.spliterator(), false)
        .map(this::toUserFeedsWithDataResponse)
        .collect(Collectors.toList());
  }

  public UserFeedsResponse updateFeed(UserFeedsRequest request)
      throws NotFoundException {
    UserFeeds userFeeds = this.userFeedsRepository
        .findById(request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setRssUrl(request.getUrl());
          return this.userFeedsRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    return toUserFeedsResponse(userFeeds);
  }

  private UserFeedsResponse toUserFeedsWithDataResponse(UserFeeds request) {
    List<String> feeds = new ArrayList<>();
    feeds.add(request.getRssUrl());
    return UserFeedsResponse.builder()
        .id(request.getId())
        .feedData(feedParserEntries(feeds))
        .logo(request.getLogo())
        .name(request.getName())
        .rssUrl(request.getRssUrl())
        .build();
  }

  private UserFeedsResponse toUserFeedsResponse(UserFeeds request) {
    return UserFeedsResponse.builder()
        .name(request.getName())
        .rssUrl(request.getRssUrl())
        .logo(request.getLogo())
        .build();
  }
}
