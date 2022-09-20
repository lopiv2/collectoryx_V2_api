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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    String getLogo = getLogoFromUrlFeed(request.getCleanUrl()).block();
    //System.out.println(getLogo);
    JSONObject jsonObject = new JSONObject(getLogo);
    UserFeeds userFeeds = UserFeeds.builder()
        .user(user)
        .name(request.getName())
        .rssUrl(request.getUrl())
        .logo(jsonObject.getJSONArray("icons").getJSONObject(0).getString("src"))
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
      try {
        URL feedUrl = new URL(args.get(0));
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
                  Locale.US);
            } else {
              formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",
                  Locale.US);
            }
            Date pubDate = formatter.parse(date);
            if(element.getElementsByTagName("media:content")!=null){
              //String im=element.getTextContent();
              System.out.println(element.getFirstChild());
            }
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

  public Mono<String> getLogoFromUrlFeed(String url) {
    WebClient client = WebClient.create("https://favicongrabber.com/api/grab/" + url);
    //WebClient client = WebClient.create("https://dc.fandom.com/api.php");
    return client
        .get()
        .uri("?action=imageserving&wisId=90286")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::is5xxServerError, response -> {
          return Mono.error(new Exception("Api error"));
        })
        .bodyToMono(String.class);
    //.publish(s -> Mono.just(s.toString()));
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
