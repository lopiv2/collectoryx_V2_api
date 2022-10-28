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
      return FandomApiReader(scrapperApiRequest);
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
        .uri(searchUriApi + Integer.parseInt(scrapperApiRequest.getSearchQuery()) + "/")
        .header(scrapperApiRequest.getHeader(), "key " + scrapperApiRequest.getKeyCode())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
  }


}
