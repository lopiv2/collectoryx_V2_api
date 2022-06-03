package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.model.CollectionTypes;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import com.collectoryx.collectoryxApi.config.service.ImageService;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

  @PostMapping(value = "/create-collection", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Mono<CollectionResponse> createCollection(
      @Parameter(description = "Name of the collection") @RequestPart("name") @NotEmpty String name,
      @Parameter(description = "Template of the collection") @RequestPart("template")
      @NotEmpty CollectionTypes template,
      @RequestPart("image") MultipartFile image) throws IOException, NotFoundException {
    CollectionTypes collectionTypes = null;
    CollectionResponse collectionResponse = null;
    CollectionRequest collectionRequest = CollectionRequest.builder()
        .name(name)
        .template(template)
        .file(image)
        .build();
    switch (template) {
      case Action_Figures:
        collectionResponse = this.collectionService.createCollection(collectionRequest);
    }
    return Mono.just(collectionResponse);
  }

}
