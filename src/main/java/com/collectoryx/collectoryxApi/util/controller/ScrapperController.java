package com.collectoryx.collectoryxApi.util.controller;

import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.util.rest.request.ScrapperApiRequest;
import com.collectoryx.collectoryxApi.util.service.ScrapperApiService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/scrapper")
@CrossOrigin
public class ScrapperController {

  private final ScrapperApiService scrapperApiService;

  public ScrapperController(ScrapperApiService scrapperApiService) {
    this.scrapperApiService = scrapperApiService;
  }

  @PostMapping(value = "/get-item-from-api/")
  public Mono<String> getCollectionItemsById(@RequestBody ScrapperApiRequest scrapperApiRequest,
      @RequestHeader(value = "Authorization") String token) {
    return this.scrapperApiService.ApiScrapper(scrapperApiRequest);
  }

  @PostMapping(value = "/get-serie-name-rebrickable")
  public Mono<String> getSerieFromRebrickable(@RequestBody ScrapperApiRequest scrapperApiRequest,
      @RequestHeader(value = "Authorization") String token) {
    Mono<String> response = this.scrapperApiService.RebrickableSeriesApiReader(scrapperApiRequest);
    return response;
  }

  @GetMapping(value = "/get-marvel-legends")
  public List<CollectionItemsResponse> getMarvel(
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionItemsResponse> collectionItemsResponseList = this.scrapperApiService.MarvelScrapper(
        query, metadata);
    return collectionItemsResponseList;
  }
}
