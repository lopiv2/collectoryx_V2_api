package com.collectoryx.collectoryxApi.util.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FandomApiService {

  public Mono<String> Prueba(String searchString) {
    WebClient client = WebClient.create("https://dc.fandom.com/api.php");
    return client
        .get()
        .uri("?action=opensearch&search="+searchString)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class);
        //.publish(s -> Mono.just("hola"));
  }

}
