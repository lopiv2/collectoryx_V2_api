package com.collectoryx.collectoryxApi.util.service;

import com.collectoryx.collectoryxApi.util.rest.request.ScrapperApiRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScrapperApiService {

  public Mono<String> ApiScrapper(ScrapperApiRequest scrapperApiRequest) {
    Mono<String> response = null;
    if (scrapperApiRequest.getUrl().contains("fandom")) {
      response = FandomApiReader(scrapperApiRequest);
    }
    if (scrapperApiRequest.getUrl().contains("pokemontcg")) {
      response = PokemonApiReader(scrapperApiRequest);
    }
    return response;
  }

  public Mono<String> FandomApiReader(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "?action=opensearch&search=";
    return client
        .get()
        .uri(searchUriApi + scrapperApiRequest.getSearchQuery())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> PokemonApiReader(ScrapperApiRequest scrapperApiRequest) {
    WebClient client = WebClient.create(scrapperApiRequest.getUrl());
    String searchUriApi = "/cards?q=name:";
    return client
        .get()
        .uri(searchUriApi + scrapperApiRequest.getSearchQuery())
        .header(scrapperApiRequest.getHeader(), scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }


}
