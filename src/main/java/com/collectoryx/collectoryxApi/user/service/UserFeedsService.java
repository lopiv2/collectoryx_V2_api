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
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jsoup.Jsoup;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

  public List<UserFeedsContentResponse> feedReader(List<String> args) {
    List<UserFeedsContentResponse> userFeedsContentResponseList = new ArrayList<>();
    boolean ok = false;
    if (args.size() == 1) {
      try {
        URL feedUrl = new URL(args.get(0));

        //SyndFeedInput input = new SyndFeedInput();
        //SyndFeed feed = input.build(new XmlReader(feedUrl));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(feedUrl.toString());

        doc.getDocumentElement().normalize();

        NodeList list = doc.getElementsByTagName("item");

        for (int temp = 0; temp < list.getLength(); temp++) {
          Node node = list.item(temp);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String image = null;
            String title = element.getElementsByTagName("title").item(0).getTextContent();
            String description = element.getElementsByTagName("description").item(0)
                .getTextContent();
            String link = element.getElementsByTagName("link").item(0).getTextContent();
            String date = element.getElementsByTagName("pubDate").item(0).getTextContent();
            DateFormat formatter = null;
            if (date.contains("GMT")) {
              formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                  Locale.ENGLISH);
            } else {
              formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",
                  Locale.ENGLISH);
            }
            Date pubDate = formatter.parse(date);
            if (description.contains("img")) {
              org.jsoup.nodes.Document docDesc = Jsoup.parse(description);
              org.jsoup.nodes.Element img = docDesc.select("img").first();
              if (img != null) {
                image = img.attr("src");
              }
            } else {
              if (element.getElementsByTagName("image").item(0) != null) {
                image = element.getElementsByTagName("image").item(0).getTextContent();
              } else {
                image = "";
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
        }
        return userFeedsContentResponseList;
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
        if (feed.getImage() != null) {
          //System.out.println(feed.getImage());
          //System.out.println(items.size());
          return userFeedsData.builder()
              .imageLink(feed.getImage().getUrl())
              .articles(items.size())
              .build();
        } else {
          //System.out.println(items.size());
          return userFeedsData.builder()
              .imageLink(null)
              .articles(items.size())
              .build();
        }

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

  private UserFeedsResponse toUserFeedsWithDataResponse(UserFeeds request) {
    List<String> feeds = new ArrayList<>();
    feeds.add(request.getRssUrl());
    return UserFeedsResponse.builder()
        .feedData(feedParserEntries(feeds))
        .name(request.getName())
        .rssUrl(request.getRssUrl())
        .build();
  }

  private UserFeedsResponse toUserFeedsResponse(UserFeeds request) {
    return UserFeedsResponse.builder()
        .name(request.getName())
        .rssUrl(request.getRssUrl())
        .build();
  }

  public UserFeedsResponse createFeed(UserFeedsRequest request)
      throws NotFoundException {
    User user = this.userRepository.findById(request.getUserId())
        .orElseThrow(NotFoundException::new);
    UserFeedsResponse userFeedsResponse = null;
    UserFeeds userFeeds = UserFeeds.builder()
        .user(user)
        .name(request.getName())
        .rssUrl(request.getUrl())
        .build();
    this.userFeedsRepository.save(userFeeds);
    userFeedsResponse = toUserFeedsResponse(userFeeds);
    return userFeedsResponse;
  }


}
