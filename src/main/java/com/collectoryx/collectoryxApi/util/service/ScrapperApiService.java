package com.collectoryx.collectoryxApi.util.service;

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

  public CollectionItemsPaginatedResponse MarvelScrapper(int page, int rowsPerPage, String query,
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
            .pages(0)
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
            //BAF
            if (metadata.toLowerCase().contains("baf")) {
              if (el.getElementsByClass("list-group-item").select("b").text().toLowerCase()
                  .contains("baf")) {
                Element zs = el.getElementsByClass("fancybox").first();
                collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                    .name(zs.text())
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
                //Add an element for each found
                collectionItemsPaginatedResponse.setTotalCount(contElements);
                //break;

                /*list.stream()
                    .skip(page * size)
                    .limit(size)
                    .collect(Collectors.toCollection(ArrayList::new));*/

                /*int sizePerPage=2;
                int page=2;

                int from = Math.max(0,page*sizePerPage);
                int to = Math.min(list.size(),(page+1)*sizePerPage)

                list.subList(from,to)*/

              }
            }
            //Set
            /*if (el.getElementsByClass("list-group-item").select("b").text().toLowerCase()
                .contains("set") && metadata.toLowerCase().contains("set")) {
              Elements zs = el.getElementsByClass("fancybox");
              collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                  .name(zs.get(0).text())
                  .build();
              break;
            }
            //Exclusive
            if (metadata.toLowerCase().contains("exclusive")) {
              collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                  .name("Exclusive")
                  .build();
              break;
            }*/
          }
        }
      }
      double pages = collectionItemsPaginatedResponse.getTotalCount() / rowsPerPage;
      collectionItemsPaginatedResponse.setPages(Integer.valueOf((int) pages));
      collectionItemsPaginatedResponse.setItems(collectionItemsResponseList);
      return collectionItemsPaginatedResponse;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getMarvelLegendsResults(String itemUrl) {
    String result = "";
    return result;
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
