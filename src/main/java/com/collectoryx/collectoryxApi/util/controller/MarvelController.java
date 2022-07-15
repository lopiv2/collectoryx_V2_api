package com.collectoryx.collectoryxApi.util.controller;

import com.collectoryx.collectoryxApi.util.service.FandomApiService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/marvel")
@CrossOrigin
public class MarvelController {

  private final FandomApiService fandomApiService;

  public MarvelController(FandomApiService fandomApiService) {
    this.fandomApiService = fandomApiService;
  }

  @GetMapping(value = "/item-images/{query}")
  public String getCollectionItemsById(@PathVariable("query") String query,
      @RequestHeader(value = "Authorization") String token) {
    this.fandomApiService.Prueba();
    return null;
    /*List<CollectionItemsResponse> collectionResponses = this.collectionService.getCollectionItemsById(
        id);*/
    //return Mono.just(collectionResponses);
  }

}
