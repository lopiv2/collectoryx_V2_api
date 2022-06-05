package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import com.collectoryx.collectoryxApi.image.service.ImageService;
import org.springframework.web.bind.annotation.CrossOrigin;
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
        collectionResponse = this.collectionService.createCollection(collectionRequest);
    }
    return Mono.just(collectionResponse);
  }

}
