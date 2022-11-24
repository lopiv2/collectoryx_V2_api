package com.collectoryx.collectoryxApi.util.service;

import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
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

  public List<CollectionItemsResponse> MarvelScrapper(String query, String metadata) {
    if (metadata.contains("baf")) {
      return MarvelReaderBaf(query);
    }
    if (metadata.contains("sets")) {
      //return "sets";
      //return MarvelReaderBaf(scrapperApiRequest);
    }
      /*if (scrapperApiRequest.getMetadata().contains("sets")) {
        return MarvelReaderSets(scrapperApiRequest);
      }
      if (scrapperApiRequest.getMetadata().contains("exclusives")) {
        return MarvelReaderExclusives(scrapperApiRequest);
      }*/
    return null;
  }

  public List<CollectionItemsResponse> MarvelReaderBaf(String query) {
    Document doc = null;
    //List of results
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    try {
      //String url = "https://www.actionfigure411.com/marvel/build-a-figure-list.php";
      String url = "https://www.actionfigure411.com/marvel/marvel-legends-everything.php";
      doc = Jsoup.connect(url).get();
      Elements gridItems = doc.getElementsByClass("grid-item");
      for (int x = 0; x < gridItems.size(); x++) {
        Elements e = gridItems.get(x)
            .getElementsByAttributeValueContaining("href", "actionfigure411");
        //if ocurrency is found
        if (e.text().toLowerCase(Locale.ROOT).contains(query)) {
          CollectionItemsResponse collectionItemsResponse = null;
          Document p = Jsoup.connect(e.attr("href")).get();
          Element listGroup = p.getElementsByClass("list-group").first();
          Element name = listGroup.select("b").first();
          Element yearParsed = listGroup.getElementsByClass("list-group-item").get(1);
          Integer year = Integer.valueOf(yearParsed.toString()
              .substring(yearParsed.toString().indexOf("Year</b>: ") + 10,
                  yearParsed.toString().indexOf("Year</b>: ") + 14));
          Element priceParsed = listGroup.getElementsByClass("list-group-item").get(1);
          int priceString = priceParsed.toString().indexOf("Retail</b>: ");
          float price = 0;
          if (priceString != -1) {
            price = Float.parseFloat(priceParsed.toString()
                .substring(priceString + 13,
                    priceString + 18));
          }
          for (int v = 0; v < p.getElementsByClass("list-group").size(); v++) {
            Element el = p.getElementsByClass("list-group").get(v);
            if (el.getElementsByClass("list-group-item").select("b").text().contains("BAF")) {
              Elements zs = el.getElementsByClass("fancybox");
              System.out.println(zs.get(0).text());
            }
          }
          /*Element serieParsed = listGroup.getElementsByClass("list-group-item").get(3);
          if (serieParsed.text().contains("Figure Seek")) {
            serieParsed = listGroup.getElementsByClass("list-group-item").get(4);
          }*/
          /*if (serieParsed.text().contains("Add to your Collection")) {
            serieParsed = listGroup.getElementsByClass("list-group-item").get(3);
          }*/
          //System.out.println(serieParsed);
          //String serie = serieParsed.select("a").first().text();
          //System.out.println(serie);
          collectionItemsResponse = CollectionItemsResponse.builder()
              .name(name.text())
              //.serie(collectionSeriesListResponse)
              .year(year)
              .price(price)
              .build();

        }
        //System.out.println(e.attr("href"));
      }

      /*Elements items = doc.getElementsByAttributeValueContaining("href",
          "baf");
      CollectionItemsResponse collectionItemsResponse = null;
      //List of all series and look for inside each one
      for (int z = 0; z < items.size(); z++) {
        Element it = items.get(z).select("a").first();
        String linkHref = it.attr("href");
        Document p = Jsoup.connect("https://www.actionfigure411.com/marvel/" + linkHref).get();
        Element its = p.getElementsByAttributeValueContaining("href",
            "checklist.php").first();
        String linkCheck = its.attr("href");
        Document checklistPage = Jsoup.connect("https://www.actionfigure411.com" + linkCheck)
            .get();
        Element serie = checklistPage.select("strong").get(0);
        Integer serieGuide = serie.text().indexOf(" - ");
        String movie = serie.text().substring(0, serieGuide);
        String serieWithoutBaf = serie.text()
            .substring(serieGuide + 2, serie.text().indexOf("BAF Checklist") - 1);

        //Create the final serie name
        String serieParsed = serieWithoutBaf + " - " + movie;

        //Select the table to get the elements of the serie
        Element table = checklistPage.select("table").get(0); //select the first table.
        Elements rows = table.select("tr");
        for (int i = 1; i < rows.size(); i++) { //Skip the first row
          Element row = rows.get(i);
          Elements cols = row.select("td");
          if (cols.get(1).text().toLowerCase(Locale.ROOT).contains(query)) {
            //System.out.println(cols.get(1).text());
            float price = 0;
            CollectionSeriesListResponse collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                .name(serieParsed)
                .build();
            if (cols.get(4).text() == "") {
              price = 0;
            } else {
              String pr = cols.get(4).text().replace("$", "");
              price = Float.parseFloat(pr);
            }
            collectionItemsResponse = CollectionItemsResponse.builder()
                .name(cols.get(1).text())
                .serie(collectionSeriesListResponse)
                .year(Integer.parseInt(cols.get(3).text()))
                .price(price)
                .build();
            collectionItemsResponseList.add(collectionItemsResponse);
            //System.out.println(collectionItemsResponse);
          }
        }
      }
      System.out.println("fin");
      return collectionItemsResponseList;*/
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*public String MarvelReader(String query) {
    Document doc = null;
    try {
      String url = "https://legendsverse.com/checklist/characters";
      doc = Jsoup.connect(url).get();
      Element d = doc.select("p[class=\"text-sm text-gray-700 leading-5\"]").first();
      //List<String> links = new ArrayList<>();
      //Pages of elements in total
      int pages = (int) Math.ceil(Double.parseDouble(d.child(2).text()) / 12);
      query = query.replaceAll(" ", "-").toLowerCase(Locale.ROOT);
      for (int z = 1; z <= pages; z++) {
        Document p = Jsoup.connect(url + "?page=" + z).get();
        Elements items = p.getElementsByAttributeValueContaining("href",
            "/checklist/characters/" + query);
        if (items.text() != null) {
          //Element lnk = items.select("a[href]").first();
          Element link = items.select("a").first();
          String linkHref = link.attr("href");
          //links.add(url + "?page=" + z);
          System.out.println("https://legendsverse.com" + linkHref);
          return getMarvelLegendsResults("https://legendsverse.com" + linkHref);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    //Retornar empty si no encuentra nada
    return doc.title();
  }*/

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
