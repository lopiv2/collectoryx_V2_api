package com.collectoryx.collectoryxApi.util.service;

import com.collectoryx.collectoryxApi.util.rest.request.ScrapperApiRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScrapperApiService {

  public Mono<String> ApiScrapper(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "";
    if (scrapperApiRequest.getUrl().contains("fandom")) {
      searchUriApi = "?action=opensearch&search=";
    }
    if (scrapperApiRequest.getUrl().contains("pokemontcg")) {
      searchUriApi = "?action=opensearch&search=";
    }
    //WebClient client = WebClient.create("https://dc.fandom.com/api.php");
    return client
        .get()
        .uri(searchUriApi + scrapperApiRequest.getSearchQuery())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
    //.publish(s -> Mono.just("hola"));
  }

}
