package com.collectoryx.collectoryxApi.util.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionMetadataType;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsPaginatedResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.util.rest.request.ScrapperApiRequest;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScrapperApiService {

  @Autowired
  private WebClient webClient;

  public Mono<String> ApiScrapper(ScrapperApiRequest scrapperApiRequest) {
    Mono<String> response = null;
    if (scrapperApiRequest.getUrl().contains("giantbomb")) {
      return GiantBombApiReader(scrapperApiRequest);
    }
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

  public CollectionItemsPaginatedResponse DCScrapper(int page, int rowsPerPage, String
      query,
      String metadata) {
    return DCReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
  }

  public CollectionItemsPaginatedResponse DCReader(int page, int rowsPerPage, String query,
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
      String url = "https://www.actionfigure411.com/dc/multiverse-price-guide.php";
      doc = Jsoup.connect(url).get();
      int contElements = 0;
      //All elements from main page
      Elements gridItems = doc.getElementsByClass("container-fluid").get(2)
          .getElementsByClass("table-responsive");
      for (int x = 0; x < gridItems.size(); x++) {
        Elements e = gridItems.get(x)
            .select("table").select("tbody").select("tr");
        String serieNew = "";
        String name = "";
        String image = "";
        Integer year = 0;
        float price = 0;
        Element ser = gridItems.get(x)
            .select("table").select("tbody").select("tr[id]").first();
        //Serie
        if (ser != null) {
          if (ser.select("font").text() != "") {
            serieNew = ser.select("font").text();
          }
        }
        for (int v = 0; v < e.size(); v++) {
          Elements it = e.get(v).select("tr");
          //System.out.println(it);
          Elements nam = it.select("td").select("a[rel]");
          //if ocurrency is found
          if (nam.text() != "" && nam.text().toLowerCase(Locale.ROOT).contains(query)) {
            CollectionItemsResponse collectionItemsResponse = null;
            //Name
            name = nam.text();
            //Image
            Document link = null;
            try {
              link = Jsoup.connect("https://www.actionfigure411.com/" + nam.attr("href"))
                  .get();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
            Element im = link.getElementsByClass("overlay-a").first();
            String linkImg = im.attr("href");
            ImageResponse imageResponse = ImageResponse.builder()
                .name(name)
                .path("https://www.actionfigure411.com" + linkImg)
                .build();
            CollectionSeriesListResponse collectionSeriesListResponse = null;
            //Year
            Element y = it.select("td").get(5);
            if (y != null) {
              year = Integer.valueOf(y.text());
            }
            //Price
            Element p = it.select("td").get(7);
            if (p != null && p.text() != "") {
              String pa = p.text().replace("$", "");
              price = Float.parseFloat(pa);
            }
            collectionSeriesListResponse = CollectionSeriesListResponse.builder()
                .name(serieNew)
                .build();
            collectionItemsResponse = CollectionItemsResponse.builder()
                .name(name)
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
        collectionItemsPaginatedResponse.setPage(page);
        int from = (page * rowsPerPage) - rowsPerPage;
        int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
        collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
        return collectionItemsPaginatedResponse;
      }
      return null;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public CollectionItemsPaginatedResponse GijoeScrapper(int page, int rowsPerPage, String query,
      String metadata) throws IOException {
    return GijoeReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
  }

  public CollectionItemsPaginatedResponse GijoeReader(int page, int rowsPerPage, String query,
      String metadata) throws IOException {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    int contElements = 0;
    String url = "";
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    String name = "";
    String serie = "";
    switch (metadata) {
      case "classified":
        url = "https://www.actionfigure411.com/gijoe/classified-checklist.php";
        serie = "Classified";
        break;
      case "retro":
        url = "https://www.actionfigure411.com/gijoe/retro-checklist.php";
        serie = "Retro";
        break;
      case "classic":
        url = "https://www.actionfigure411.com/gijoe/classic-checklist.php";
        serie = "Classic";
        break;
      case "ultimates":
        url = "https://www.actionfigure411.com/gijoe/super7-checklist.php";
        serie = "Ultimates";
        break;
      case "reaction":
        url = "https://www.actionfigure411.com/gijoe/super7-reaction-checklist.php";
        serie = "ReAction";
        break;
      case "25":
        url = "https://www.actionfigure411.com/gijoe/25th-anniversary-checklist.php";
        serie = "25 Anniversary";
        break;
      default:
        return null;
    }
    collectionSeriesListResponse = CollectionSeriesListResponse.builder()
        .name(serie)
        .build();
    doc = Jsoup.connect(url).get();
    Elements e = doc
        .getElementsByAttributeValueContaining("href", "actionfigure411");

    for (int v = 0; v < e.size(); v++) {
      if (e.get(v).text().toLowerCase(Locale.ROOT).contains((query))) {
        List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
        CollectionItemMetadataResponse collectionItemMetadataWaveResponse = CollectionItemMetadataResponse.builder()
            .name("Wave")
            .value(e.get(v).parent().nextElementSibling().text())
            .type(CollectionMetadataType.STRING)
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataWaveResponse);
        name = e.get(v).text();
        CollectionItemsResponse collectionItemsResponse = null;
        Document p = Jsoup.connect(e.get(v).attr("href")).get();
        //Image
        Element im = p.getElementsByClass("overlay-a").first();
        String linkImg = im.attr("href");
        //Year
        Element listGroup = p.getElementsByClass("list-group").get(1);
        Element yearParsed = listGroup.getElementsByClass("list-group-item").get(1);
        Integer year=0;
        if(StringUtils.isNumeric(yearParsed.toString())){
          year = Integer.valueOf(yearParsed.toString()
              .substring(yearParsed.toString().indexOf("Year</b>: ") + 10,
                  yearParsed.toString().indexOf("Year</b>: ") + 14));
        }
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
            .name(name)
            .path("https://www.actionfigure411.com" + linkImg)
            .build();
        collectionItemsResponse = CollectionItemsResponse.builder()
            .name(name)
            .serie(collectionSeriesListResponse)
            .year(year)
            .metadata(collectionItemMetadataResponseList)
            .image(imageResponse)
            .price(price)
            .build();
        contElements++;
        collectionItemsResponseList.add(collectionItemsResponse);
        collectionItemsPaginatedResponse.setTotalCount(contElements);
      }
    }

    collectionItemsPaginatedResponse.setPage(page);
    int from = (page * rowsPerPage) - rowsPerPage;
    int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
    collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
    return collectionItemsPaginatedResponse;
  }

  public CollectionItemsPaginatedResponse HotWheelsScrapper(int page, int rowsPerPage, String query,
      String metadata) {
    return HotWheelsReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
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
          Element price = body.get(r).select("td").get(14);
          float priceVal = 0;
          if (price.text() == "" || price == null) {
            priceVal = 0;
          } else {
            priceVal = Float.valueOf(
                price.text().replace("$", ""));
          }
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
              .price(priceVal)
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
        if (result != null) {
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
        } else {
          return null;
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

  public CollectionItemsPaginatedResponse MarvelScrapper(int page, int rowsPerPage, String
      query,
      String metadata) {
    return MarvelReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
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
          Element listGroup = p.getElementsByClass("list-group").get(1);
          Element name = listGroup.select("b").first();
          Element yearParsed = listGroup.getElementsByClass("list-group-item").get(1);
          //Year
          Integer year=0;
          if(StringUtils.isNumeric(yearParsed.toString())){
            year = Integer.valueOf(yearParsed.toString()
                .substring(yearParsed.toString().indexOf("Year</b>: ") + 10,
                    yearParsed.toString().indexOf("Year</b>: ") + 14));
          }
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

  public CollectionItemsPaginatedResponse MineralScrapper(int page, int rowsPerPage, String
      query,
      String metadata) throws IOException {
    return MineralReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
  }

  public CollectionItemsPaginatedResponse MineralReader(int page, int rowsPerPage, String query,
      String metadata) throws IOException {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    String url = "https://en.wikipedia.org/wiki/List_of_minerals";
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    CollectionItemsResponse collectionItemsResponse = null;
    int contElements = 0;
    String name = "";
    String serie = "Standard";
    float price = 0;
    Integer year = Year.now().getValue();
    doc = Jsoup.connect(url).get();
    Element body = doc.getElementById("bodyContent");
    Element division = body.getElementsByClass("mw-parser-output").first();
    Elements minerals = division.getElementsByAttributeValueContaining("title", query);
    if (minerals != null) {
      for (int x = 0; x < minerals.size(); x++) {
        name = StringUtils.capitalize(minerals.get(x).text());
        Document p = Jsoup.connect(minerals.get(x).attr("abs:href"))
            .userAgent(
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
            .get();
        Element infobox = p.getElementsByClass("infobox").first();
        String linkImg = "";
        String formula = "";
        String ima = "";
        String color = "";
        ImageResponse imageResponse = null;
        List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
        if (infobox != null) {
          Element imageBox = infobox.getElementsByClass("infobox-image").first();
          if (imageBox != null) {
            linkImg = imageBox.select("a").first().select("img").attr("abs:src");
          } else {
            linkImg = "";
          }
          Elements labels = infobox.select("tr");
          for (int z = 0; z < labels.size(); z++) {
            if (labels.get(z).select("a").text().contains("Formula")) {
              Element f = labels.get(z).select("td").first();
              formula = f.text();
              int hasLink = formula.indexOf("[");
              //If it has link after formula or IMA, remove it
              if (hasLink != -1) {
                formula = formula.substring(0, formula.indexOf("["));
              }
            }
            if (labels.get(z).select("a").text().contains("IMA")) {
              Element f = labels.get(z).select("td").first();
              ima = f.text();
              int hasLink = ima.indexOf("[");
              if (hasLink != -1) {
                ima = ima.substring(0, ima.indexOf("["));
              }
            }
            if (labels.get(z).select("th").text().contains("Color")) {
              Element f = labels.get(z).select("td").first();
              color = f.text();
            }
          }
        }
        imageResponse = ImageResponse.builder()
            .name(name)
            .path(linkImg)
            .build();
        CollectionItemMetadataResponse collectionItemMetadataFormulaResponse = CollectionItemMetadataResponse.builder()
            .name("Formula")
            .value(formula)
            .type(CollectionMetadataType.STRING)
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataFormulaResponse);
        CollectionItemMetadataResponse collectionItemMetadataImaResponse = CollectionItemMetadataResponse.builder()
            .name("IMA symbol")
            .value(ima)
            .type(CollectionMetadataType.STRING)
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataImaResponse);
        CollectionItemMetadataResponse collectionItemMetadataColorResponse = CollectionItemMetadataResponse.builder()
            .name("Color")
            .value(color)
            .type(CollectionMetadataType.STRING)
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataColorResponse);
        collectionSeriesListResponse = CollectionSeriesListResponse.builder()
            .name(serie)
            .build();
        collectionItemsResponse = CollectionItemsResponse.builder()
            .name(name)
            .serie(collectionSeriesListResponse)
            .year(year)
            .metadata(collectionItemMetadataResponseList)
            .image(imageResponse)
            .price(price)
            .build();
        contElements++;
        collectionItemsResponseList.add(collectionItemsResponse);
        collectionItemsPaginatedResponse.setTotalCount(contElements);
      }
      List<CollectionItemsResponse> deDupResponseList = collectionItemsResponseList.stream()
          .distinct().collect(Collectors.toList());
      collectionItemsPaginatedResponse.setTotalCount(deDupResponseList.size());
      collectionItemsPaginatedResponse.setPage(page);
      int from = (page * rowsPerPage) - rowsPerPage;
      int to = Math.min(deDupResponseList.size(), ((page * rowsPerPage)));
      collectionItemsPaginatedResponse.setItems(deDupResponseList.subList(from, to));
      return collectionItemsPaginatedResponse;
    } else {
      return null;
    }
  }

  public CollectionItemsPaginatedResponse MotuScrapper(int page, int rowsPerPage, String
      query,
      String metadata) throws IOException {
    return MotuReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
  }

  public CollectionItemsPaginatedResponse MotuReader(int page, int rowsPerPage, String query,
      String metadata) throws IOException {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    int contElements = 0;
    String url = "";
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    String name = "";
    String serie = "";
    switch (metadata) {
      case "origins":
        url = "https://www.actionfigure411.com/masters-of-the-universe/origins-checklist.php";
        serie = "Origins";
        break;
      case "original":
        url = "https://www.actionfigure411.com/masters-of-the-universe/original-checklist.php";
        serie = "Original";
        break;
      case "classics":
        url = "https://www.actionfigure411.com/masters-of-the-universe/mattel-classics-checklist.php";
        serie = "Classics";
        break;
      case "masterverse":
        url = "https://www.actionfigure411.com/masters-of-the-universe/masterverse-checklist.php";
        serie = "Masterverse";
        break;
      case "super7":
        url = "https://www.actionfigure411.com/masters-of-the-universe/super7-checklist.php";
        serie = "Super7";
        break;
      default:
        return null;
    }
    collectionSeriesListResponse = CollectionSeriesListResponse.builder()
        .name(serie)
        .build();
    doc = Jsoup.connect(url).get();
    Elements e = doc
        .getElementsByAttributeValueContaining("href", "actionfigure411");
    for (int v = 0; v < e.size(); v++) {
      if (e.get(v).text().toLowerCase(Locale.ROOT).contains((query))) {
        name = e.get(v).text();
        CollectionItemsResponse collectionItemsResponse = null;
        Document p = Jsoup.connect(e.get(v).attr("href")).get();
        //Image
        Element im = p.getElementsByClass("overlay-a").first();
        String linkImg = im.attr("href");
        //Year
        Element listGroup = p.getElementsByClass("list-group").get(1);
        Element yearParsed = listGroup.getElementsByClass("list-group-item").get(1);
        Integer year=0;
        if(StringUtils.isNumeric(yearParsed.toString())){
          year = Integer.valueOf(yearParsed.toString()
              .substring(yearParsed.toString().indexOf("Year</b>: ") + 10,
                  yearParsed.toString().indexOf("Year</b>: ") + 14));
        }
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
            .name(name)
            .path("https://www.actionfigure411.com" + linkImg)
            .build();
        collectionItemsResponse = CollectionItemsResponse.builder()
            .name(name)
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

    collectionItemsPaginatedResponse.setPage(page);
    int from = (page * rowsPerPage) - rowsPerPage;
    int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
    collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
    return collectionItemsPaginatedResponse;
  }

  public CollectionItemsPaginatedResponse StarWarsScrapper(int page, int rowsPerPage, String
      query,
      String metadata) throws IOException {
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse = StarWarsReader(page,
        rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
    return collectionItemsPaginatedResponse;
  }

  public CollectionItemsPaginatedResponse StarWarsReader(int page, int rowsPerPage, String query,
      String metadata) throws IOException {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    int contElements = 0;
    String url = "https://www.jeditemplearchives.com/content/";
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    CollectionItemsResponse collectionItemsResponse = null;
    String name = "";
    String serie = "";
    float price = 0;
    Integer year = 0;
    Response response =
        Jsoup.connect(
                url + "modules.php?name=JReviews&rop=search&query=")
            .userAgent("Mozilla/5.0")
            .timeout(10 * 1000)
            .method(Method.POST)
            .data("query", query)
            .data("search", "")
            .followRedirects(true)
            .execute();

    //parse the document from response
    Document document = Jsoup.parse(response.body());
    Element searchBox = document.getElementsByClass("rdr-index").first();
    if (searchBox != null) {
      Elements results = searchBox.select("strong");
      //Number of elements per collection
      for (int x = 0; x < results.size(); x++) {
        Element n = results.get(x).select("strong").select("a").first();
        name = n.text().substring(0, n.text().indexOf(" -"));
        Element item = results.get(x).getElementsByAttributeValueContaining("href", "modules.php")
            .first();
        Document p = Jsoup.connect(url + item.attr("href"))
            .get();
        Element it = p.getElementsByClass("RDRBody").first();
        if (it.text().indexOf("Collection:") != -1 && it.text().indexOf("Number:") != -1
            && it.text().indexOf("Availability:") != -1 && it.text().indexOf("License:") != -1) {
          //name = it.text().substring(it.text().indexOf("Name:") + 6, it.text().indexOf("Collection"));
          serie = it.text()
              .substring(it.text().indexOf("Collection:") + 12, it.text().indexOf("Number:"));
          String y = it.text()
              .substring(it.text().indexOf("Availability:") + 14, it.text().indexOf("License:"));
          if (y.contains("TBD")) {
            y = String.valueOf(Year.now().getValue());
          }
          y = y.replace("*", "");
          y = y.replaceAll("[^0-9]", "");
          if(StringUtils.isNumeric(y)){
            year = Integer.valueOf(y.substring(0, 4));
          }
          else{
            year=0;
          }
          Element pr = it.select("p:contains(Retail)").first();
          if (pr.text().indexOf("Retail:") != -1) {
            price = Float.parseFloat(
                pr.text().substring(pr.text().indexOf("Retail:") + 9, pr.text().indexOf(" USD")));
          } else {
            price = 0;
          }
          Element imageBox = p.getElementsByClass("imagebox").first();
          String linkImg = "";
          if (imageBox != null) {
            linkImg = imageBox.select("td").last().select("a").attr("href");
          }
          Document im = Jsoup.connect("https://www.jeditemplearchives.com" + linkImg).get();
          Element imgLink = im.getElementsByAttributeValueContaining("src", ".jpg").first();
          ImageResponse imageResponse = ImageResponse.builder()
              .name(name)
              .path(imgLink.attr("abs:src").replace("..", ""))
              .build();
          List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
          CollectionItemMetadataResponse collectionItemMetadataBrandResponse = CollectionItemMetadataResponse.builder()
              .name("Brand")
              .value("Hasbro")
              .type(CollectionMetadataType.STRING)
              .build();
          collectionItemMetadataResponseList.add(collectionItemMetadataBrandResponse);
          collectionSeriesListResponse = CollectionSeriesListResponse.builder()
              .name(serie)
              .build();
          collectionItemsResponse = CollectionItemsResponse.builder()
              .name(name)
              .serie(collectionSeriesListResponse)
              .year(year)
              .metadata(collectionItemMetadataResponseList)
              .image(imageResponse)
              .price(price)
              .build();
          contElements++;
          collectionItemsResponseList.add(collectionItemsResponse);
          collectionItemsPaginatedResponse.setTotalCount(contElements);
        }
      }
      collectionItemsPaginatedResponse.setPage(page);
      int from = (page * rowsPerPage) - rowsPerPage;
      int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
      collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
      return collectionItemsPaginatedResponse;
    } else {
      return null;
    }
  }

  public CollectionItemsPaginatedResponse TMNTScrapper(int page, int rowsPerPage, String query,
      String metadata)
      throws IOException {
    return TMNTReader(page, rowsPerPage, query.toLowerCase(Locale.ROOT), metadata);
  }

  private CollectionItemsPaginatedResponse TMNTReader(int page, int rowsPerPage, String query,
      String metadata)
      throws IOException {
    Document doc = null;
    //List of results
    CollectionItemsPaginatedResponse collectionItemsPaginatedResponse =
        CollectionItemsPaginatedResponse.builder()
            .items(null)
            .page(0)
            .totalCount(0)
            .build();
    List<CollectionItemsResponse> collectionItemsResponseList = new ArrayList<>();
    int contElements = 0;
    String url = "";
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    String name = "";
    String serie = "";
    switch (metadata) {
      case "neca":
        url = "https://www.actionfigure411.com/teenage-mutant-ninja-turtles/neca-checklist.php";
        serie = "NECA";
        break;
      case "super7":
        url = "https://www.actionfigure411.com/teenage-mutant-ninja-turtles/super7-checklist.php";
        serie = "Super7";
        break;
      case "playmates":
        url = "https://www.actionfigure411.com/teenage-mutant-ninja-turtles/playmates-checklist.php";
        serie = "Playmates";
        break;
      default:
        return null;
    }
    collectionSeriesListResponse = CollectionSeriesListResponse.builder()
        .name(serie)
        .build();
    doc = Jsoup.connect(url).get();
    Elements e = doc
        .getElementsByAttributeValueContaining("href", "actionfigure411");

    for (int v = 0; v < e.size(); v++) {
      if (e.get(v).text().toLowerCase(Locale.ROOT).contains((query))) {
        List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
        CollectionItemMetadataResponse collectionItemMetadataWaveResponse = CollectionItemMetadataResponse.builder()
            .name("Wave")
            .value(e.get(v).parent().nextElementSibling().text())
            .type(CollectionMetadataType.STRING)
            .build();
        collectionItemMetadataResponseList.add(collectionItemMetadataWaveResponse);
        name = e.get(v).text();
        CollectionItemsResponse collectionItemsResponse = null;
        Document p = Jsoup.connect(e.get(v).attr("href")).get();
        //Image
        Element im = p.getElementsByClass("overlay-a").first();
        String linkImg = im.attr("href");
        //Year
        Element listGroup = p.getElementsByClass("list-group").get(1);
        Element yearParsed = listGroup.getElementsByClass("list-group-item").get(1);
        Integer year=0;
        if(StringUtils.isNumeric(yearParsed.toString())){
          year = Integer.valueOf(yearParsed.toString()
              .substring(yearParsed.toString().indexOf("Year</b>: ") + 10,
                  yearParsed.toString().indexOf("Year</b>: ") + 14));
        }

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
            .name(name)
            .path("https://www.actionfigure411.com" + linkImg)
            .build();
        collectionItemsResponse = CollectionItemsResponse.builder()
            .name(name)
            .serie(collectionSeriesListResponse)
            .year(year)
            .metadata(collectionItemMetadataResponseList)
            .image(imageResponse)
            .price(price)
            .build();
        contElements++;
        collectionItemsResponseList.add(collectionItemsResponse);
        collectionItemsPaginatedResponse.setTotalCount(contElements);
      }
    }
    collectionItemsPaginatedResponse.setPage(page);
    int from = (page * rowsPerPage) - rowsPerPage;
    int to = Math.min(collectionItemsResponseList.size(), ((page * rowsPerPage)));
    collectionItemsPaginatedResponse.setItems(collectionItemsResponseList.subList(from, to));
    return collectionItemsPaginatedResponse;
  }

  public Mono<String> PokemonApiReader(ScrapperApiRequest scrapperApiRequest) {
    //WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String query = scrapperApiRequest.getSearchQuery();
    if (scrapperApiRequest.getSearchQuery().contains(" ")) {
      query = "\"" + query + "\"";
    }
    String searchUriApi = "/cards?q=name:";
    return webClient
        .get()
        .uri(scrapperApiRequest.getUrl() + searchUriApi + query + "&page="
            + scrapperApiRequest.getPage() + "&pageSize=" + scrapperApiRequest.getRowsPerPage())
        .header(scrapperApiRequest.getHeader(), scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> GiantBombApiReader(ScrapperApiRequest scrapperApiRequest) {
    //WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String query = scrapperApiRequest.getSearchQuery();
    if (scrapperApiRequest.getSearchQuery().contains(" ")) {
      query = "\"" + query + "\"";
    }
    String searchUriApi = "/search/";
    return webClient
        .get()
        .uri(scrapperApiRequest.getUrl() + searchUriApi + "?query=" + query + "&resources=game"
            + "&page="
            + scrapperApiRequest.getPage() + "&limit=" + scrapperApiRequest.getRowsPerPage()
            + "&api_key=" + scrapperApiRequest.getKeyCode() + "&format=json")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> RebrickableApiReader(ScrapperApiRequest scrapperApiRequest) {
    //WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/sets/";
    return webClient
        .get()
        .uri(scrapperApiRequest.getUrl() + searchUriApi + "?page=" + scrapperApiRequest.getPage()
            + "&page_size="
            + scrapperApiRequest.getRowsPerPage()
            + "&search=" + scrapperApiRequest.getSearchQuery())
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> RebrickableMinifigsApiReader(ScrapperApiRequest scrapperApiRequest) {
    //WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/minifigs/";
    return webClient
        .get()
        .uri(scrapperApiRequest.getUrl() + searchUriApi + "?page=" + scrapperApiRequest.getPage()
            + "&page_size="
            + scrapperApiRequest.getRowsPerPage()
            + "&search=" + scrapperApiRequest.getSearchQuery())
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> RebrickableSeriesApiReader(ScrapperApiRequest scrapperApiRequest) {
    //WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/themes/";
    return webClient
        .get()
        .uri(scrapperApiRequest.getUrl() + searchUriApi + scrapperApiRequest.getSearchQuery() + "/")
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

}
