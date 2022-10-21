package com.collectoryx.collectoryxApi.image.controller;

import com.collectoryx.collectoryxApi.collections.rest.request.CollectionSerieListRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import com.collectoryx.collectoryxApi.image.rest.request.ImageRequest;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.image.service.ImageService;
import com.collectoryx.collectoryxApi.page.rest.request.PageFrontRequest;
import com.collectoryx.collectoryxApi.page.rest.response.PagingResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import java.io.IOException;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/images")
@CrossOrigin
public class ImageController {

  private final ImageService imageService;
  private final CollectionService collectionService;

  public ImageController(ImageService imageService, CollectionService collectionService) {
    this.imageService = imageService;
    this.collectionService = collectionService;
  }

  @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Mono<ImageResponse> putImage(
      @Parameter(description = "Name of the image") @RequestPart("name") @NotEmpty String name,
      @Parameter(description = "Content of the image",
          content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
      @RequestPart("image") MultipartFile image) throws IOException {
    ImageResponse imageResponse = this.imageService.createImage(name, image);
    //ImageResponse imageResponse=ImageResponse.builder().name("hola").path("hola").build();
    return Mono.just(imageResponse);
  }

  @PutMapping(value = "/create-serie", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Mono<CollectionSeriesListResponse> putImageForSerie(
      @Parameter(description = "Name of the image") @RequestPart("name") @NotEmpty String name,
      @Parameter(description = "Name of the image") @RequestPart("collection") @NotEmpty String collection,
      @Parameter(description = "Content of the image",
          content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
      @RequestPart("image") MultipartFile image) throws IOException {
    ImageResponse imageResponse = this.imageService.createImage(name, image);
    CollectionSeriesListResponse collectionSerieListResponse = null;
    CollectionSerieListRequest collectionSerieListRequest = CollectionSerieListRequest.builder()
        .collection(Long.valueOf(collection))
        .name(name)
        .path(imageResponse.getPath())
        .build();
    try {
      collectionSerieListResponse = this.collectionService.createSerie(collectionSerieListRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(collectionSerieListResponse);
  }

  @DeleteMapping(value = "/delete-image/{id}")
  public Mono<Boolean> deleteImage(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.imageService.deleteImage(id);
    return Mono.just(isDeleted);
  }

  @PostMapping(value = "/get-images-local")
  public Mono<PagingResponse> getAllLocalImages(@RequestBody PageFrontRequest pageFrontRequest,
      @RequestHeader(value = "Authorization") String token) {
    PagingResponse<ImageResponse> imageResponses = null;
    if (pageFrontRequest.getSearch() != null) {
      imageResponses =
          this.imageService.getLocalImagesSearchQuery(pageFrontRequest);
    } else {
      imageResponses = this.imageService.getLocalImages(
          pageFrontRequest);
    }
    return Mono.just(imageResponses);
  }

  @PutMapping(value = "/update-image")
  public Mono<ImageResponse> updateCollection(
      @RequestBody ImageRequest imageRequest,
      @RequestHeader(value = "Authorization") String token) {
    ImageResponse imageResponse = null;
    try {
      imageResponse = this.imageService.updateImage(imageRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(imageResponse);
  }
}
