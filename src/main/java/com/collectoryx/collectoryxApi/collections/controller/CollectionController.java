package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.rest.request.CollectionCreateItemRequest;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

  @GetMapping(value = "/collections/{id}")
  public Mono<List<CollectionItemsResponse>> getCollectionById(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionItemsResponse> collectionResponses = this.collectionService.getCollectionById(
        id);
    return Mono.just(collectionResponses);
  }

  @GetMapping(value = "/count-collections")
  public Mono<Long> getCountCollections(@RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService.getCountOfCollections();
    return Mono.just(count);
  }

  @GetMapping(value = "/count-collections-items")
  public Mono<Long> getCountCollectionsItems(
      @RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService.getCountOfCollectionItems();
    return Mono.just(count);
  }

  @GetMapping(value = "/count-collections-money")
  public Mono<List<CollectionItemsResponse>> getCollectionsTotalMoney(
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionItemsResponse> collectionResponses = this.collectionService
        .getMoneyFromAllItems();
    return Mono.just(collectionResponses);
  }

  @PostMapping(value = "/create-collection")
  public Mono<CollectionResponse> createCollection(
      @RequestBody CollectionRequest collectionRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionResponse collectionResponse = null;
    switch (collectionRequest.getTemplate()) {
      case New:
        try {
          collectionResponse = this.collectionService.createCollectionNew(collectionRequest);
        } catch (NotFoundException e) {
          e.printStackTrace();
        }
        break;
      case Action_Figures:
        try {
          collectionResponse = this.collectionService.createCollection(collectionRequest);
        } catch (NotFoundException e) {
          e.printStackTrace();
        }
        break;
    }
    return Mono.just(collectionResponse);
  }

  @PostMapping(value = "/create-item")
  public Mono<CollectionItemsResponse> createItem(
      @RequestBody CollectionCreateItemRequest collectionCreateItemRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsResponse collectionItemsResponse = null;
    try {
      if (collectionCreateItemRequest.getAdquiringDate() != null) {
        collectionItemsResponse = this.collectionService.createItem(collectionCreateItemRequest);
      }
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return Mono.just(collectionItemsResponse);
  }

  @PostMapping(value = "/create-serie")
  public Mono<CollectionSeriesListResponse> createSerie(
      @RequestBody CollectionSerieListRequest collectionSerieRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionSeriesListResponse collectionSerieListResponse = null;
    try {
      collectionSerieListResponse = this.collectionService.createSerie(collectionSerieRequest);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return Mono.just(collectionSerieListResponse);
  }

  @DeleteMapping(value = "/delete-collection-item/{id}")
  public Mono<Boolean> deleteCollectionItem(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.collectionService.deleteCollectionItem(id);
    return Mono.just(isDeleted);
  }

  @DeleteMapping(value = "/delete-collection/{id}")
  public Mono<Boolean> deleteCollection(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.collectionService.deleteCollection(id);
    return Mono.just(isDeleted);
  }

  @GetMapping(value = "/get-item/{id}")
  public Mono<CollectionItemsResponse> getCollectionItem(
      @RequestHeader(value = "Authorization") String token, @PathVariable("id") Long id)
      throws NotFoundException {
    CollectionItemsResponse collectionItemsResponse = null;
    if (id != null) {
      collectionItemsResponse = this.collectionService.getCollectionItem(id);
    }
    return Mono.just(collectionItemsResponse);
  }

  @PostMapping(value = "/toggle-item-own")
  public Mono<CollectionItemsResponse> toggleItemOwn(
      @RequestBody CollectionItemRequest collectionItemRequest,
      @RequestHeader(value = "Authorization") String token) {
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

  @PostMapping(value = "/toggle-item-wish")
  public Mono<CollectionItemsResponse> toggleItemWish(
      @RequestBody CollectionItemRequest collectionItemRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsResponse collectionItemsResponse = null;
    if (collectionItemRequest.getId() != null) {
      try {
        collectionItemsResponse = this.collectionService.toggleWish(collectionItemRequest);
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    return Mono.just(collectionItemsResponse);
  }

  @PutMapping(value = "/update-item")
  public Mono<CollectionItemsResponse> updateItem(
      @RequestBody CollectionCreateItemRequest collectionCreateItemRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsResponse collectionItemsResponse = null;
    try {
      if (collectionCreateItemRequest.getAdquiringDate() != null) {
        collectionItemsResponse = this.collectionService.updateItem(collectionCreateItemRequest);
      }
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return Mono.just(collectionItemsResponse);
  }

  @GetMapping(value = "/view-collections")
  public Mono<List<CollectionSeriesListResponse>> getCollections(
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionSeriesListResponse> collectionListResponses =
        this.collectionService.listCollections();
    return Mono.just(collectionListResponses);
  }

  @GetMapping(value = "/view-series")
  public Mono<List<CollectionSeriesListResponse>> getAllSeries(
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionSeriesListResponse> collectionSerieListResponses = this.collectionService
        .listAllSeriesCollections();
    return Mono.just(collectionSerieListResponses);
  }

  @GetMapping(value = "/view-collection-series/{id}")
  public Mono<List<CollectionSeriesListResponse>> getCollectionsSeries(
      @PathVariable("id") Long id, @RequestHeader(value = "Authorization") String token) {
    List<CollectionSeriesListResponse> collectionSerieListResponses = this.collectionService
        .listSeriesByCollection(id);
    return Mono.just(collectionSerieListResponses);
  }

}
