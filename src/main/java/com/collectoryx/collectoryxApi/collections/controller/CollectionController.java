package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionListResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import com.collectoryx.collectoryxApi.image.service.ImageService;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping(value = "/view-collections")
  public Mono<List<CollectionListResponse>> getCollections() {
    List<CollectionListResponse> collectionListResponses = this.collectionService.listCollections();
    return Mono.just(collectionListResponses);
  }

  @GetMapping(value = "/collections/{id}")
  public Mono<List<CollectionItemsResponse>> getCollectionById(
      @RequestParam(required = true) Long id) {
    List<CollectionItemsResponse> collectionResponses = this.collectionService.getCollectionById(
        id);
    return Mono.just(collectionResponses);
  }

}
