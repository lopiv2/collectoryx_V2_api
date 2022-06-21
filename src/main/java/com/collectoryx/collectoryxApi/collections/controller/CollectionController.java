package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.rest.request.CollectionItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionSerieListRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import com.collectoryx.collectoryxApi.image.service.ImageService;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class CollectionController {

  private final CollectionService collectionService;
  private final ImageService imageService;

  public CollectionController(CollectionService collectionService, ImageService imageService) {
    this.collectionService = collectionService;
    this.imageService = imageService;
  }


  @PostMapping(value = "/create-collection")
  public Mono<CollectionResponse> createCollection(
      @RequestBody CollectionRequest collectionRequest) {
    CollectionResponse collectionResponse = null;
    switch (collectionRequest.getTemplate()) {
      case Action_Figures:
        if (collectionRequest.getFile() != null) {
          try {
            collectionResponse = this.collectionService.createCollection(collectionRequest);
          } catch (NotFoundException e) {
            e.printStackTrace();
          }
        }
    }
    return Mono.just(collectionResponse);
  }

  @PostMapping(value = "/create-serie")
  public Mono<CollectionSeriesListResponse> createSerie(
      @RequestBody CollectionSerieListRequest collectionSerieRequest) {
    CollectionSeriesListResponse collectionSerieListResponse = null;
    if (collectionSerieRequest.getFile() != null) {
      try {
        collectionSerieListResponse = this.collectionService.createSerie(collectionSerieRequest);
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    return Mono.just(collectionSerieListResponse);
  }

  @PostMapping(value = "/toggle-item-own")
  public Mono<CollectionItemsResponse> toggleItemOwn(
      @RequestBody CollectionItemRequest collectionItemRequest) {
    CollectionItemsResponse collectionItemsResponse = null;
    if (collectionItemRequest.getId() != null) {
      try {
        collectionItemsResponse = this.collectionService.toggleOwn(collectionItemRequest);
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    return Mono.just(collectionItemsResponse);
  }


  @GetMapping(value = "/count-collections")
  public Mono<Long> getCountCollections() {
    Long count = this.collectionService.getCountOfCollections();
    return Mono.just(count);
  }

  @GetMapping(value = "/count-collections-money")
  public Mono<List<CollectionItemsResponse>> getCollectionsTotalMoney() {
    List<CollectionItemsResponse> collectionResponses = this.collectionService
        .getMoneyFromAllItems();
    return Mono.just(collectionResponses);
  }

  @GetMapping(value = "/count-collections-items")
  public Mono<Long> getCountCollectionsItems() {
    Long count = this.collectionService.getCountOfCollectionItems();
    return Mono.just(count);
  }

  @GetMapping(value = "/view-collections")
  public Mono<List<CollectionSeriesListResponse>> getCollections() {
    List<CollectionSeriesListResponse> collectionListResponses = this.collectionService.listCollections();
    return Mono.just(collectionListResponses);
  }

  @GetMapping(value = "/view-series")
  public Mono<List<CollectionSeriesListResponse>> getCollectionsSeries() {
    List<CollectionSeriesListResponse> collectionSerieListResponses = this.collectionService
        .listSeriesCollections();
    return Mono.just(collectionSerieListResponses);
  }

  @GetMapping(value = "/collections/{id}")
  public Mono<List<CollectionItemsResponse>> getCollectionById(@PathVariable("id") Long id) {
    List<CollectionItemsResponse> collectionResponses = this.collectionService.getCollectionById(
        id);
    return Mono.just(collectionResponses);
  }

  @DeleteMapping(value = "/delete-collection/{id}")
  public Mono<Boolean> deleteCollectionItem(@PathVariable("id") Long id) throws NotFoundException {
    boolean isDeleted = this.collectionService.deleteCollection(id);
    return Mono.just(isDeleted);
  }

}
