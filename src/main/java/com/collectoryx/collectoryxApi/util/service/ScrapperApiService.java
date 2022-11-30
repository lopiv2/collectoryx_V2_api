package com.collectoryx.collectoryxApi.util.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionMetadataType;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsPaginatedResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.util.rest.request.ScrapperApiRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScrapperApiService {

  public Mono<String> ApiScrapper(ScrapperApiRequest scrapperApiRequest) {
    Mono<String> response = null;
    if (scrapperApiRequest.getUrl().contains("pokemontcg")) {
      return PokemonApiReader(scrapperApiRequest);
    }
    if (scrapperApiRequest.getUrl().contains("rebrickable")) {
      if (scrapperApiRequest.getMetadata().contains("sets")) {
        return RebrickableApiReader(scrapperApiRequest);
      }
      if (scrapperApiRequest.getMetadata().contains("minifigs")) {
        return RebrickableMinifigsApiReader(scrapperApiRequest);
      }
      if (scrapperApiRequest.getMetadata().contains("series")) {
        return RebrickableSeriesApiReader(scrapperApiRequest);
      }
    }
    return null;
  }

  public CollectionItemsPaginatedResponse HotWheelsScrapper(int page, int rowsPerPage, String query,
      String metadata) {
    return HotWheelsReader(page, rowsPerPage, query, metadata);
  }

  public CollectionItemsPaginatedResponse HotWheelsReader(int page, int rowsPerPage, String query,
      String metadata) {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    try {
      String url = "https://collecthw.com/";
      Element j = Jsoup.connect(url + "/hw/search/" + query).get();
      Element res = j.getElementsByClass("container-fluid google-auto-placed").first()
          .getElementsByClass("searchtitle").first();
      CollectionItemsResponse collectionItemsResponse = null;
      CollectionSeriesListResponse collectionSeriesListResponse = null;
      int contElements = 0;
      if (res != null) { //Multiple elements
        Elements body = j.getElementsByClass("container-fluid google-auto-placed").first()
            .select("tbody").select("tr");
        for (int r = 0; r < body.size(); r++) {
          Element name = body.get(r).select("td").get(3);
          Element serie = body.get(r).select("td").get(5);
          Element price = body.get(r).select("td").get(13);
          Element year = body.get(r).select("td").get(4);
          String image = body.get(r).select("td").get(0).select("a").attr("src");
          List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
          CollectionItemMetadataResponse collectionItemMetadataColResponse = CollectionItemMetadataResponse.builder()
              .name("Collection Number")
              .value(body.get(r).select("td").get(1).text().replace("col:", ""))
              .type(CollectionMetadataType.STRING)
              .build();
          collectionItemMetadataResponseList.add(collectionItemMetadataColResponse);
          CollectionItemMetadataResponse collectionItemMetadataColorResponse = CollectionItemMetadataResponse.builder()
              .name("Color")
              .type(CollectionMetadataType.STRING)
              .value(body.get(r).select("td").get(6).text().replace("color:", ""))
              .build();
          collectionItemMetadataResponseList.add(collectionItemMetadataColorResponse);
          ImageResponse imageResponse = ImageResponse.builder()
              .name(name.text().replace("model:", ""))
              .path(image)
              .build();
          collectionSeriesListResponse = CollectionSeriesListResponse.builder()
              .name(serie.text().replace("series:", ""))
              .build();
          collectionItemsResponse = CollectionItemsResponse.builder()
              .name(name.text().replace("model:", ""))
              .serie(collectionSeriesListResponse)
              .year(Integer.valueOf(year.text().replace("year:", "")))
              .image(imageResponse)
              .metadata(collectionItemMetadataResponseList)
              .price(Float.valueOf(
                  price.text().replace("$", "")))
              .build();
          //System.out.println(collectionItemsResponse);
          contElements++;
          collectionItemsResponseList.add(collectionItemsResponse);
          collectionItemsPaginatedResponse.setTotalCount(contElements);
        }
      } else //Single Element result
      {
        Element result = j.getElementsByClass("container-fluid google-auto-placed").first()
            .getElementsByClass("row searchcontent").first();
        List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
        Document col = Jsoup.connect(url + result.select("a").first().attr("href")).get();
        CollectionItemMetadataResponse collectionItemMetadataColResponse = CollectionItemMetadataResponse.builder()
            .name("Collection Number")
            .value(col.select("tbody").first().select("td").get(1).text().replace("col:", ""))
            .type(CollectionMetadataType.STRING)
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataColResponse);
        CollectionItemMetadataResponse collectionItemMetadataColorResponse = CollectionItemMetadataResponse.builder()
            .name("Color")
            .type(CollectionMetadataType.STRING)
            .value(result.select("dd").get(3).text().replace("color:", ""))
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataColorResponse);
        //Name
        Element name = result.select("a").first();
        //Price
        Document p = Jsoup.connect(url + name.attr("href")).get();
        //Image
        ImageResponse imageResponse = ImageResponse.builder()
            .name(name.text())
            .path(result.select("img").first().attr("src"))
            .build();
        //Year
        Element year = result.select("a").get(1);
        //Serie
        Element serie = result.select("a").get(2);
        collectionSeriesListResponse = CollectionSeriesListResponse.builder()
            .name(serie.text())
            .build();
        collectionItemsResponse = CollectionItemsResponse.builder()
            .name(name.text())
            .serie(collectionSeriesListResponse)
            .year(Integer.valueOf(year.text()))
            .image(imageResponse)
            .metadata(collectionItemMetadataResponseList)
            .price(Float.valueOf(
                p.select("tbody").first().select("td").last().text()
                    .replace("$", "")))
            .build();
        contElements++;
        collectionItemsResponseList.add(collectionItemsResponse);
        collectionItemsPaginatedResponse.setTotalCount(contElements);
      }
      collectionItemsPaginatedResponse.setPage(page);
      int from = (page * rowsPerPage) - rowsPerPage;
      int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
      collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
      return collectionItemsPaginatedResponse;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public CollectionItemsPaginatedResponse MarvelScrapper(int page, int rowsPerPage, String
      query,
      String metadata) {
    return MarvelReader(page, rowsPerPage, query, metadata);
  }

  public CollectionItemsPaginatedResponse MarvelReader(int page, int rowsPerPage, String query,
      String metadata) {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    try {
      String url = "https://www.actionfigure411.com/marvel/marvel-legends-everything.php";
      doc = Jsoup.connect(url).get();
      int contElements = 0;
      //All elements from main page
      Elements gridItems = doc.getElementsByClass("grid-item");
      for (int x = 0; x < gridItems.size(); x++) {
        Elements e = gridItems.get(x)
            .getElementsByAttributeValueContaining("href", "actionfigure411");
        //if ocurrency is found
        if (e.text().toLowerCase(Locale.ROOT).contains(query)) {
          CollectionItemsResponse collectionItemsResponse = null;
          Document p = Jsoup.connect(e.attr("href")).get();
          //Image
          Element im = p.getElementsByClass("overlay-a").first();
          String linkImg = im.attr("href");
          //Name
          Element listGroup = p.getElementsByClass("list-group").first();
          Element name = listGroup.select("b").first();
          Element yearParsed = listGroup.getElementsByClass("list-group-item").get(1);
          //Year
          Integer year = Integer.valueOf(yearParsed.toString()
              .substring(yearParsed.toString().indexOf("Year</b>: ") + 10,
                  yearParsed.toString().indexOf("Year</b>: ") + 14));
          Element priceParsed = listGroup.getElementsByClass("list-group-item").get(1);
          int priceString = priceParsed.toString().indexOf("Retail</b>: ");
          //Price
          float price = 0;
          if (priceString != -1) {
            String pri = priceParsed.toString()
                .substring(priceString + 13,
                    priceString + 18);
            String str = pri.replaceAll("[^\\d.]", "");
            price = Float.parseFloat(str);
          }
          ImageResponse imageResponse = ImageResponse.builder()
              .name(name.text())
              .path("https://www.actionfigure411.com" + linkImg)
              .build();
          CollectionSeriesListResponse collectionSeriesListResponse = null;
          //Serie
          for (int v = 0; v < p.getElementsByClass("list-group").size(); v++) {
            Element el = p.getElementsByClass("list-group").get(v);
            if (metadata.contains("baf")) {
              Element b = el.select("b:matches((?i)BAF)").first();
              if (b != null) {
                collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                    .name(el.getElementsByClass("fancybox").first().text())
                    .build();
                collectionItemsResponse = CollectionItemsResponse.builder()
                    .name(name.text())
                    .serie(collectionSeriesListResponse)
                    .year(year)
                    .image(imageResponse)
                    .price(price)
                    .build();
                contElements++;
                collectionItemsResponseList.add(collectionItemsResponse);
                collectionItemsPaginatedResponse.setTotalCount(contElements);
              }
            }
            if (metadata.contains("set")) {
              Element s = el.select("b:matches((?i)Set)").first();
              Element b = el.select("b:matches((?i)BAF)").first();
              if (s != null && b == null) {
                collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                    .name(el.getElementsByClass("fancybox").first().text())
                    .build();
                collectionItemsResponse = CollectionItemsResponse.builder()
                    .name(name.text())
                    .serie(collectionSeriesListResponse)
                    .year(year)
                    .image(imageResponse)
                    .price(price)
                    .build();
                contElements++;
                collectionItemsResponseList.add(collectionItemsResponse);
                collectionItemsPaginatedResponse.setTotalCount(contElements);
              }
            }
            if (metadata.contains("exclusives")) {
              Element s = el.select("b:matches((?i)Set)").first();
              Element b = el.select("b:matches((?i)BAF)").first();
              //If there is no set and baf, it´s exclusive
              if (s == null && b == null) {
                collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                    .name("Exclusives")
                    .build();
                collectionItemsResponse = CollectionItemsResponse.builder()
                    .name(name.text())
                    .serie(collectionSeriesListResponse)
                    .year(year)
                    .image(imageResponse)
                    .price(price)
                    .build();
                contElements++;
                collectionItemsResponseList.add(collectionItemsResponse);
                collectionItemsPaginatedResponse.setTotalCount(contElements);
              }
            }
          }
        }
      }
      collectionItemsPaginatedResponse.setPage(page);
      int from = (page * rowsPerPage) - rowsPerPage;
      int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
      collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
      return collectionItemsPaginatedResponse;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Mono<String> PokemonApiReader(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/cards?q=name:";
    return client
        .get()
        .uri(searchUriApi + scrapperApiRequest.getSearchQuery() + "&page="
            + scrapperApiRequest.getPage() + "&pageSize=" + scrapperApiRequest.getRowsPerPage())
        .header(scrapperApiRequest.getHeader(), scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> RebrickableApiReader(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/sets/";
    return client
        .get()
        .uri(searchUriApi + "?page=" + scrapperApiRequest.getPage() + "&page_size="
            + scrapperApiRequest.getRowsPerPage()
            + "&search=" + scrapperApiRequest.getSearchQuery())
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> RebrickableMinifigsApiReader(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/minifigs/";
    return client
        .get()
        .uri(searchUriApi + "?page=" + scrapperApiRequest.getPage() + "&page_size="
            + scrapperApiRequest.getRowsPerPage()
            + "&search=" + scrapperApiRequest.getSearchQuery())
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> RebrickableSeriesApiReader(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/themes/";
    return client
        .get()
        .uri(searchUriApi + scrapperApiRequest.getSearchQuery() + "/")
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }


}
