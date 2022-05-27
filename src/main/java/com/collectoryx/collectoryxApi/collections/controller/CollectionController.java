package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.model.CollectionTypes;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import javax.validation.Valid;
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

  public CollectionController(CollectionService collectionService) {
    this.collectionService = collectionService;
  }

  @PostMapping("/create-collection")
  public Mono<CollectionResponse> createCollection(@RequestBody @Valid CollectionRequest request) {
    CollectionTypes collectionTypes = null;
    CollectionResponse collectionResponse=null;
    switch (request.getTemplate()) {
      case Action_Figures:
        collectionResponse = this.collectionService
            .createCollection(request);
    }
    return Mono.just(collectionResponse);
  }
}
