package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.Collection;
import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.model.CollectionMetadata;
import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionMetadataRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionSeriesListRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionSerieListRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
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
  private final CollectionSeriesListRepository collectionSeriesListRepository;

  public CollectionService(CollectionRepository collectionRepository,
      CollectionListRepository collectionListRepository,
      ImageRepository imagesRepository, CollectionMetadataRepository collectionMetadataRepository,
      CollectionSeriesListRepository collectionSeriesListRepository) {
    this.collectionRepository = collectionRepository;
    this.collectionListRepository = collectionListRepository;
    this.imagesRepository = imagesRepository;
    this.collectionMetadataRepository = collectionMetadataRepository;
    this.collectionSeriesListRepository = collectionSeriesListRepository;
  }

  public long getCountOfCollections() {
    long count = this.collectionListRepository.count();
    return count;
  }

  public long getCountOfCollectionItems() {
    long count = this.collectionRepository.count();
    return count;
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

  public CollectionItemsResponse toggleOwn(CollectionItemRequest item) throws NotFoundException {
    Collection collection = this.collectionRepository.findById(item.getId())
        .orElseThrow(NotFoundException::new);
    collection.setOwn(!item.getOwn());
    this.collectionRepository.save(collection);
    CollectionItemsResponse collectionItemsResponse=toCollectionItemResponse(collection);
    return collectionItemsResponse;
  }

  public CollectionSeriesListResponse createSerie(CollectionSerieListRequest request)
      throws NotFoundException {
    Image image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
        NotFoundException::new);
    ImageResponse imageResponse = toImageResponse(image);
    CollectionSeriesList collectionSeriesList = CollectionSeriesList.builder()
        .name(request.getName())
        .logo(image)
        .build();
    this.collectionSeriesListRepository.save(collectionSeriesList);
    CollectionSeriesListResponse collectionSeriesListResponse = CollectionSeriesListResponse
        .builder()
        .name(request.getName())
        .logo(imageResponse).build();
    return collectionSeriesListResponse;
  }

  public List<CollectionSeriesListResponse> listCollections() {
    final List<CollectionSeriesListResponse> collectionListResponseList = new LinkedList<>();
    List<CollectionList> collections = this.collectionListRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionSeriesListResponse> listSeriesCollections() {
    final List<CollectionSeriesListResponse> collectionSerieListResponseList = new LinkedList<>();
    List<CollectionSeriesList> collections = this.collectionSeriesListRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionSerieListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getCollectionById(Long id) {
    final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<Collection> collections = this.collectionRepository
        .findByCollection_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemsResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getMoneyFromAllItems() {
    final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<Collection> collections = this.collectionRepository
        .findByCollection_Id(id);
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
    collectionMetadata = StreamSupport.stream(this.collectionMetadataRepository.findByCollection_Id(
            collection.getId()).spliterator(), false).map(this::toCollectionMetadataResponse)
        .collect(Collectors.toList());
    return CollectionItemsResponse.builder()
        .id(collection.getId())
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

  private CollectionSeriesListResponse toCollectionSerieListResponse(
      CollectionSeriesList collection) {
    ImageResponse image = null;
    try {
      image = toImageResponse(
          this.imagesRepository.findById(collection.getLogo().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return CollectionSeriesListResponse.builder()
        .id(collection.getId())
        .name(collection.getName())
        .logo(image)
        .build();
  }

  private CollectionSeriesListResponse toCollectionListResponse(
      CollectionList collection) {
    ImageResponse image = null;
    try {
      image = toImageResponse(
          this.imagesRepository.findById(collection.getLogo().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return CollectionSeriesListResponse.builder()
        .id(collection.getId())
        .name(collection.getName())
        .logo(image)
        .build();
  }

  private CollectionItemsResponse toCollectionItemResponse(Collection collection) {
    return CollectionItemsResponse.builder()
        .id(collection.getId())
        .name(collection.getName())
        .adquiringDate(collection.getAdquiringDate())
        .notes(collection.getNotes())
        .price(collection.getPrice())
        .year(collection.getYear())
        .collection(collection.getCollection().getName())
        .serie(collection.getSerie())
        .own(collection.isOwn())
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
