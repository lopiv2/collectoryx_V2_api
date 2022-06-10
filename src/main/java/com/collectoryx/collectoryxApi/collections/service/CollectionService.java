package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.Collection;
import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.model.CollectionMetadata;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionMetadataRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionListResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final CollectionListRepository collectionListRepository;
  private final ImageRepository imagesRepository;
  private final CollectionMetadataRepository collectionMetadataRepository;

  public CollectionService(CollectionRepository collectionRepository,
      CollectionListRepository collectionListRepository,
      ImageRepository imagesRepository, CollectionMetadataRepository collectionMetadataRepository) {
    this.collectionRepository = collectionRepository;
    this.collectionListRepository = collectionListRepository;
    this.imagesRepository = imagesRepository;
    this.collectionMetadataRepository = collectionMetadataRepository;
  }

  public CollectionResponse createCollection(CollectionRequest request) throws NotFoundException {
    Image image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
        NotFoundException::new);
    CollectionList collectionList = CollectionList.builder()
        .name(request.getName())
        .logo(image)
        .build();
    this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = CollectionResponse.builder().collection(
            request.getName())
        .template(request.getTemplate()).logo(image).build();
    return collectionResponse;
  }

  public List<CollectionListResponse> listCollections() {
    final List<CollectionListResponse> collectionListResponseList = new LinkedList<>();
    List<CollectionList> collections = this.collectionListRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getCollectionById(Long id) {
    final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<Collection> collections = this.collectionRepository
        .findByCollection(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemsResponse)
        .collect(Collectors.toList());
  }

  private CollectionItemsResponse toCollectionItemsResponse(Collection collection) {
    ImageResponse image = null;
    try {
      image = toImageResponse(
          this.imagesRepository.findById(collection.getImage().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    List<CollectionMetadataResponse> collectionMetadata = null;
    collectionMetadata = StreamSupport.stream(this.collectionMetadataRepository.findByCollection(
            collection.getId()).spliterator(), false).map(this::toCollectionMetadataResponse)
        .collect(Collectors.toList());
    return CollectionItemsResponse.builder()
        .name(collection.getName())
        .image(image)
        .collection(collection.getName())
        .serie(collection.getSerie())
        .year(collection.getYear())
        .price(collection.getPrice())
        .own(collection.isOwn())
        .notes(collection.getNotes())
        .adquiringDate(collection.getAdquiringDate())
        .metadata(collectionMetadata)
        .build();
  }

  private CollectionListResponse toCollectionListResponse(
      CollectionList collection) {
    ImageResponse image = null;
    try {
      image = toImageResponse(
          this.imagesRepository.findById(collection.getLogo().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return CollectionListResponse.builder()
        .id(collection.getId())
        .name(collection.getName())
        .logo(image)
        .build();
  }

  private CollectionResponse toCollectionResponse(CollectionList request,
      CollectionRequest collectionRequest) {
    return CollectionResponse.builder()
        .collection(request.getName())
        .logo(request.getLogo())
        .template((collectionRequest.getTemplate()))
        .build();
  }

  private CollectionMetadataResponse toCollectionMetadataResponse(CollectionMetadata request) {
    return CollectionMetadataResponse.builder()
        .value(request.getValue())
        .name(request.getName())
        .build();
  }

  private ImageResponse toImageResponse(Image request) {
    return ImageResponse.builder()
        .name(request.getName())
        .path(request.getPath())
        .created(request.getCreated())
        .build();
  }

}
