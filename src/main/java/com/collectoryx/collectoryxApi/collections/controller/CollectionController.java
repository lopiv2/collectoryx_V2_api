package com.collectoryx.collectoryxApi.collections.controller;

import com.collectoryx.collectoryxApi.collections.model.CollectionTemplateType;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionCreateItemImportApiRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionCreateItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionSerieListRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CSVHeadersResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionListResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.collections.service.CollectionService;
import com.collectoryx.collectoryxApi.image.service.ImageService;
import com.collectoryx.collectoryxApi.page.rest.request.PageFrontRequest;
import com.collectoryx.collectoryxApi.page.rest.response.PagingResponse;
import com.collectoryx.collectoryxApi.util.service.ScrapperApiService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class CollectionController {

  private final CollectionService collectionService;
  private final ImageService imageService;

  private final ScrapperApiService scrapperApiService;


  public CollectionController(CollectionService collectionService, ImageService imageService,
      ScrapperApiService scrapperApiService) {
    this.collectionService = collectionService;
    this.imageService = imageService;
    this.scrapperApiService = scrapperApiService;
  }

  @PostMapping(value = "/collections")
  public Mono<PagingResponse<CollectionItemsResponse>> getCollectionItemsById(
      @RequestBody PageFrontRequest pageFrontRequest,
      @RequestHeader(value = "Authorization") String token) {
    PagingResponse<CollectionItemsResponse> collectionResponses = null;
    if (pageFrontRequest.getSearch() != null) {
      collectionResponses = this.collectionService.
          getCollectionItemsByIdSearchQuery(pageFrontRequest);
    } else {
      collectionResponses = this.collectionService.
          getCollectionItemsById(pageFrontRequest);
    }
    return Mono.just(collectionResponses);
  }

  @PostMapping(value = "/collections/get-item-data")
  public Mono<CollectionItemsResponse> getCollectionItemByData(
      @RequestBody CollectionItemRequest collectionItemRequest,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    PagingResponse<CollectionItemsResponse> collectionResponses = null;
    CollectionItemsResponse collectionItemsResponse = this.collectionService.getCollectionItemByData(
        collectionItemRequest);
    if (collectionItemsResponse != null) {
      return Mono.just(collectionItemsResponse);
    } else {
      return null;
    }
  }

  @PostMapping(value = "/collections/recent")
  public Mono<PagingResponse<CollectionItemsResponse>> getRecentCollectionItemsById(
      @RequestBody PageFrontRequest pageFrontRequest,
      @RequestHeader(value = "Authorization") String token) {
    PagingResponse<CollectionItemsResponse> collectionResponses = null;
    collectionResponses = this.collectionService.
        getAllCollectionItemsByUserId(pageFrontRequest);
    return Mono.just(collectionResponses);
  }

  @GetMapping(value = "/count-collections/{id}")
  public Mono<Long> getCountCollectionsById(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService.getCountOfCollections(id);
    return Mono.just(count);
  }

  @GetMapping(value = "/count-collections-items/{id}")
  public Mono<Long> getCountCollectionsItemsById(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService.getCountOfCollectionItems(id);
    return Mono.just(count);
  }

  @GetMapping(value = "/count-collections-money/{id}")
  public Mono<Long> getCollectionsTotalMoneyById(
      @PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService
        .getMoneyFromAllItems(id);
    return Mono.just(count);
  }

  @GetMapping(value = "/count-collections-wishlist/{id}")
  public Mono<Long> getWishlistSize(
      @PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService.getCountOfWishlist(id);
    return Mono.just(count);
  }

  @PostMapping(value = "/count-completed-collections")
  public Mono<Long> getCompletedCollections(
      @RequestBody PageFrontRequest pageFrontRequest,
      @RequestHeader(value = "Authorization") String token) {
    Long count = this.collectionService.getCountOfCompletedCollections(pageFrontRequest);
    return Mono.just(count);
  }

  @PostMapping(value = "/create-collection")
  public Mono<CollectionResponse> createCollection(
      @RequestBody CollectionRequest collectionRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionResponse collectionResponse = null;
    CollectionTemplateType template = (CollectionTemplateType) collectionRequest.getTemplate();
    switch (template) {
      case New:
        try {
          collectionResponse = this.collectionService.createCollectionNew(collectionRequest);
        } catch (NotFoundException e) {
          throw new RuntimeException(e);
        }
        break;
      case Action_Figures:
        try {
          collectionResponse = this.collectionService.createCollection(collectionRequest);
        } catch (NotFoundException e) {
          throw new RuntimeException(e);
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
      if (collectionCreateItemRequest.getAcquiringDate() != null) {
        collectionItemsResponse = this.collectionService.createItem(collectionCreateItemRequest);
      }
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(collectionItemsResponse);
  }

  @PostMapping(value = "/create-item-new-serie")
  public Mono<Void> createItemNewSerie(
      @RequestBody CollectionCreateItemImportApiRequest collectionCreateItemRequest,
      @RequestHeader(value = "Authorization") String token) {
    try {
      this.collectionService.createItemNewSerie(collectionCreateItemRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.empty();
    //return Mono.just(collectionItemsResponse);
  }

  @PostMapping(value = "/create-serie")
  public Mono<CollectionSeriesListResponse> createSerie(
      @RequestBody CollectionSerieListRequest collectionSerieRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionSeriesListResponse collectionSerieListResponse = null;
    try {
      collectionSerieListResponse = this.collectionService.createSerie(collectionSerieRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
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

  @DeleteMapping(value = "/delete-collection-cascade/{id}")
  public Mono<Boolean> deleteCollectionCascade(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.collectionService.deleteCollectionCascade(id);
    return Mono.just(isDeleted);
  }

  @DeleteMapping(value = "/delete-serie/{id}")
  public Mono<Boolean> deleteSerie(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) throws NotFoundException {
    boolean isDeleted = this.collectionService.deleteSerie(id);
    return Mono.just(isDeleted);
  }

  @GetMapping(value = "/get-collection/{id}")
  public Mono<CollectionListResponse> getCollectionById(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    CollectionListResponse collectionListResponse =
        this.collectionService.getCollectionById(id);
    return Mono.just(collectionListResponse);
  }

  @GetMapping(value = "/get-images/{query}")
  public Mono<String> getImagesByStringDDG(@PathVariable("query") String query,
      @RequestHeader(value = "Authorization") String token) {
    //List<String> response = this.collectionService.getImagesFromDDG(query);
    String response = "";
    return Mono.just(response);
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

  @GetMapping(value = "/get-items-per-year/{id}")
  public Mono<List<CollectionItemsResponse>> getItemsPerYear(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionItemsResponse> collectionItemsResponses =
        this.collectionService.getItemsYear(id);
    return Mono.just(collectionItemsResponses);
  }

  @GetMapping(value = "/get-metadata-fields/{id}")
  public Mono<List<CollectionMetadataResponse>> getMetadataFields(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionMetadataResponse> collectionMetadataResponseList =
        this.collectionService.getMetadataFields(id);
    return Mono.just(collectionMetadataResponseList);
  }

  @PutMapping(value = "/import-file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Mono<List<CSVHeadersResponse>> importFile(
      @Parameter(description = "Name of the image") @RequestPart("name") @NotEmpty String name,
      @Parameter(description = "Content of the file",
          content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
      @RequestPart("file") MultipartFile file) {
    List<CSVHeadersResponse> records = this.collectionService.getCSVHeaders(file);
    return Mono.just(records);
  }

  @GetMapping(value = "/most-valuable-item/{id}")
  public Mono<CollectionItemsResponse> getMostValuableItem(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsResponse collectionItemsResponse = this.collectionService.getMostValuableItem(
        id);
    return Mono.just(collectionItemsResponse);
  }

  @PostMapping(value = "/parse-file")
  public Mono<Integer> parseFile(
      @RequestBody String fileJSON,
      @RequestHeader(value = "Authorization") String token) {
    int cont = this.collectionService.parseCSV(fileJSON);
    return Mono.just(cont);
  }

  @PostMapping(value = "/toggle-collection-ambit")
  public Mono<CollectionResponse> toggleCollectionAmbit(
      @RequestBody CollectionRequest collectionRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionResponse collectionResponse = null;
    if (collectionRequest.getId() != null) {
      try {
        collectionResponse = this.collectionService.toggleAmbit(collectionRequest);
      } catch (NotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return Mono.just(collectionResponse);
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
        throw new RuntimeException(e);
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
        throw new RuntimeException(e);
      }
    }
    return Mono.just(collectionItemsResponse);
  }

  @PutMapping(value = "/update-collection")
  public Mono<CollectionResponse> updateCollection(
      @RequestBody CollectionRequest collectionRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionResponse collectionResponse = null;
    try {
      collectionResponse = this.collectionService.updateCollection(collectionRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(collectionResponse);
  }

  @PutMapping(value = "/update-item")
  public Mono<CollectionItemsResponse> updateItem(
      @RequestBody CollectionCreateItemRequest collectionCreateItemRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionItemsResponse collectionItemsResponse = null;
    try {
      if (collectionCreateItemRequest.getAcquiringDate() != null) {
        collectionItemsResponse = this.collectionService.updateItem(collectionCreateItemRequest);
      }
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(collectionItemsResponse);
  }

  @PutMapping(value = "/update-serie")
  public Mono<CollectionSeriesListResponse> updateSerie(
      @RequestBody CollectionSerieListRequest collectionSerieListRequest,
      @RequestHeader(value = "Authorization") String token) {
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    try {
      collectionSeriesListResponse = this.collectionService.updateSerie(collectionSerieListRequest);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
    return Mono.just(collectionSeriesListResponse);
  }

  @PostMapping(value = "/view-collections")
  public Mono<PagingResponse> getCollections(@RequestBody PageFrontRequest pageFrontRequest,
      @RequestHeader(value = "Authorization") String token) {
    PagingResponse<CollectionListResponse> collectionListResponsePagingResponse = null;
    if (pageFrontRequest.getSearch() != null) {
      collectionListResponsePagingResponse =
          this.collectionService.listCollectionsSearchQuery(pageFrontRequest);
    } else {
      collectionListResponsePagingResponse = this.collectionService.listCollections(
          pageFrontRequest);
    }
    return Mono.just(collectionListResponsePagingResponse);
  }

  @GetMapping(value = "/view-series/{id}")
  public Mono<List<CollectionSeriesListResponse>> getAllSeries(@PathVariable("id") Long id,
      @RequestHeader(value = "Authorization") String token) {
    List<CollectionSeriesListResponse> collectionSerieListResponses = this.collectionService
        .listAllSeriesCollections(id);
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
