package com.collectoryx.collectoryxApi.util.controller;

import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsPaginatedResponse;
import com.collectoryx.collectoryxApi.util.rest.request.ScrapperApiRequest;
import com.collectoryx.collectoryxApi.util.service.ScrapperApiService;
import java.io.IOException;
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

  @GetMapping(value = "/get-hot-wheels")
  public CollectionItemsPaginatedResponse getHotWheels(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .HotWheelsScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }

  @GetMapping(value = "/get-marvel-legends")
  public CollectionItemsPaginatedResponse getMarvel(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .MarvelScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }

  @GetMapping(value = "/get-minerals")
  public CollectionItemsPaginatedResponse getMinerals(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) throws IOException {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .MineralScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }

  @GetMapping(value = "/get-motu")
  public CollectionItemsPaginatedResponse getMotu(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) throws IOException {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .MotuScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }

  @GetMapping(value = "/get-dc-multiverse")
  public CollectionItemsPaginatedResponse getDC(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .DCScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }

  @GetMapping(value = "/get-gijoe")
  public CollectionItemsPaginatedResponse getGijoe(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) throws IOException {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .GijoeScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }

  @GetMapping(value = "/get-star-wars")
  public CollectionItemsPaginatedResponse getStarWars(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) throws IOException {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .StarWarsScrapper(page, rowsPerPage, query, metadata);
    if (collectionItemsResponseList != null) {
      return collectionItemsResponseList;
    }
    else{
      return null;
    }
  }

  @GetMapping(value = "/get-tmnt")
  public CollectionItemsPaginatedResponse getTMNT(
      @RequestParam int page,
      @RequestParam int rowsPerPage,
      @RequestParam String query,
      @RequestParam String metadata,
      @RequestHeader(value = "Authorization") String token) throws IOException {
    CollectionItemsPaginatedResponse collectionItemsResponseList = this.scrapperApiService
        .TMNTScrapper(page, rowsPerPage, query, metadata);
    return collectionItemsResponseList;
  }
}
