package com.collectoryx.collectoryxApi.util.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class FandomApiService {

  public void Prueba() {
    //WebClient client = WebClient.create("https://marveltoys.fandom.com/api.php");
    WebClient client = WebClient.create("https://dc.fandom.com/api.php");
    client
        .get()
        .uri("?action=imageserving&wisId=90286")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class)
        .doOnNext(responses -> System.out.println(responses))
        .block();
  }

}
