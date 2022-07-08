package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionItem;
import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.model.CollectionMetadata;
import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionItemRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionMetadataRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionSeriesListRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionCreateItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionSerieListRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionListResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import java.util.ArrayList;
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

  private final CollectionItemRepository collectionItemRepository;
  private final CollectionListRepository collectionListRepository;
  private final ImageRepository imagesRepository;
  private final CollectionMetadataRepository collectionMetadataRepository;
  private final CollectionSeriesListRepository collectionSeriesListRepository;

  public CollectionService(CollectionItemRepository collectionItemRepository,
      CollectionListRepository collectionListRepository,
      ImageRepository imagesRepository, CollectionMetadataRepository collectionMetadataRepository,
      CollectionSeriesListRepository collectionSeriesListRepository) {
    this.collectionItemRepository = collectionItemRepository;
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
    long count = this.collectionItemRepository.count();
    return count;
  }

  public CollectionResponse createCollection(CollectionRequest request) throws NotFoundException {
    Image image = null;
    CollectionList collectionList = null;
    if (request.getFile() != null) {
      image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
          NotFoundException::new);
      collectionList = CollectionList.builder()
          .name(request.getName())
          .logo(image)
          .build();
    } else {
      collectionList = CollectionList.builder()
          .name(request.getName())
          .build();
    }
    this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = CollectionResponse.builder()
        .collection(request.getName())
        .template(request.getTemplate())
        .logo(image)
        .build();
    return collectionResponse;
  }

  public CollectionResponse createCollectionNew(CollectionRequest request)
      throws NotFoundException {
    Image image = null;
    CollectionList collectionList = null;
    if (request.getFile() != null) {
      image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
          NotFoundException::new);
      collectionList = CollectionList.builder()
          .name(request.getName())
          .logo(image)
          .build();
    } else {
      collectionList = CollectionList.builder()
          .name(request.getName())
          .build();
    }
    this.collectionListRepository.save(collectionList);
    if (request.getMetadata().size() > 0) {
      List<CollectionMetadata> collectionMetadataList = new ArrayList<>();
      for (CollectionMetadata m : request.getMetadata()) {
        CollectionMetadata collectionMetadata = CollectionMetadata.builder()
            .id(m.getId())
            .name(m.getName())
            .type(m.getType())
            .value("")
            .collection(collectionList)
            .build();
        collectionMetadataList.add(collectionMetadata);
      }
      this.collectionMetadataRepository.saveAll(collectionMetadataList);
    }
    CollectionResponse collectionResponse = CollectionResponse.builder()
        .collection(request.getName())
        .template(request.getTemplate())
        .logo(image)
        .build();
    return collectionResponse;
  }

  public CollectionItemsResponse toggleOwn(CollectionItemRequest item) throws NotFoundException {
    CollectionItem collection = this.collectionItemRepository.findById(item.getId())
        .orElseThrow(NotFoundException::new);
    collection.setOwn(!item.getOwn());
    this.collectionItemRepository.save(collection);
    CollectionItemsResponse collectionItemsResponse = toCollectionItemResponse(collection);
    return collectionItemsResponse;
  }

  public CollectionItemsResponse toggleWish(CollectionItemRequest item) throws NotFoundException {
    CollectionItem collection = this.collectionItemRepository.findById(item.getId())
        .orElseThrow(NotFoundException::new);
    collection.setWanted(!item.getWanted());
    this.collectionItemRepository.save(collection);
    CollectionItemsResponse collectionItemsResponse = toCollectionItemResponse(collection);
    return collectionItemsResponse;
  }

  public CollectionItemsResponse getCollectionItem(Long id) throws NotFoundException {
    CollectionItem col = this.collectionItemRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    CollectionItemsResponse collectionItemsResponse = toCollectionItemResponse(col);
    return collectionItemsResponse;
  }

  public CollectionItemsResponse createItem(CollectionCreateItemRequest request)
      throws NotFoundException {
    Image image = null;
    ImageResponse imageResponse = null;
    if (request.getImage() != null) {
      image = this.imagesRepository.findImageByPath(request.getImage()).orElseThrow(
          NotFoundException::new);
      imageResponse = toImageResponse(image);
    }
    CollectionList collectionList = null;
    CollectionListResponse collectionListResponse = null;
    collectionList = this.collectionListRepository.findById(request.getCollection())
        .orElseThrow(NotFoundException::new);
    collectionListResponse = toCollectionListResponse(collectionList);

    CollectionSeriesList collectionSeriesList = null;
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    collectionSeriesList = this.collectionSeriesListRepository.findById(request.getSerie())
        .orElseThrow(NotFoundException::new);
    collectionSeriesListResponse = toCollectionSerieListResponse(collectionSeriesList);
    CollectionItem collectionItem = null;
    CollectionItemsResponse collectionItemsResponse = null;
    if (imageResponse != null) {
      collectionItem = CollectionItem.builder()
          .name(request.getName())
          .serie(collectionSeriesList)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .image(image)
          .collection(collectionList)
          .build();
      collectionItemsResponse = CollectionItemsResponse.builder()
          .name(request.getName())
          .serie(collectionSeriesListResponse)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .image(imageResponse)
          .collection(collectionListResponse)
          .build();

    } else {
      collectionItem = CollectionItem.builder()
          .name(request.getName())
          .serie(collectionSeriesList)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .collection(collectionList)
          .build();
      collectionItemsResponse = CollectionItemsResponse.builder()
          .name(request.getName())
          .serie(collectionSeriesListResponse)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .collection(collectionListResponse)
          .build();
    }
    this.collectionItemRepository.save(collectionItem);

    return collectionItemsResponse;
  }

  public CollectionSeriesListResponse createSerie(CollectionSerieListRequest request)
      throws NotFoundException {
    Image image = null;
    ImageResponse imageResponse = null;
    if (request.getFile() != null) {
      image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
          NotFoundException::new);
      imageResponse = toImageResponse(image);
    }
    CollectionList collectionList = this.collectionListRepository.findById(
        request.getCollection()).orElseThrow(
        NotFoundException::new);
    CollectionResponse collectionResponse = toCollectionResponse(collectionList);
    CollectionSeriesList collectionSeriesList = CollectionSeriesList.builder()
        .name(request.getName())
        .logo(image)
        .collection(collectionList)
        .build();
    this.collectionSeriesListRepository.save(collectionSeriesList);
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    if (imageResponse != null) {
      collectionSeriesListResponse = CollectionSeriesListResponse
          .builder()
          .name(request.getName())
          .collection(collectionResponse)
          .logo(imageResponse)
          .build();
    } else {
      collectionSeriesListResponse = CollectionSeriesListResponse
          .builder()
          .name(request.getName())
          .collection(collectionResponse)
          .build();
    }

    return collectionSeriesListResponse;
  }

  public CollectionItemsResponse updateItem(CollectionCreateItemRequest request)
      throws NotFoundException {
    Image image = null;
    ImageResponse imageResponse = null;
    if (request.getImage() != null) {
      image = this.imagesRepository.findImageByPath(request.getImage()).orElseThrow(
          NotFoundException::new);
      imageResponse = toImageResponse(image);
    }
    final Image imageRight = image;
    CollectionList collectionList = null;
    CollectionListResponse collectionListResponse = null;
    collectionList = this.collectionListRepository.findById(request.getCollection())
        .orElseThrow(NotFoundException::new);
    collectionListResponse = toCollectionListResponse(collectionList);

    CollectionSeriesListResponse collectionSeriesListResponse = null;
    final CollectionSeriesList collectionSeriesList = this.collectionSeriesListRepository.findById(
            request.getSerie())
        .orElseThrow(NotFoundException::new);
    collectionSeriesListResponse = toCollectionSerieListResponse(collectionSeriesList);
    CollectionItem collectionItem = this.collectionItemRepository.findById(request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setSerie(collectionSeriesList);
          item.setPrice(request.getPrice());
          item.setYear(request.getYear());
          item.setAdquiringDate(request.getAdquiringDate());
          item.setOwn(request.isOwn());
          item.setNotes(request.getNotes());
          item.setImage(imageRight);
          return this.collectionItemRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    CollectionItemsResponse collectionItemsResponse = null;
    collectionItemsResponse = CollectionItemsResponse.builder()
        .name(request.getName())
        .serie(collectionSeriesListResponse)
        .price(request.getPrice())
        .year(request.getYear())
        .adquiringDate(request.getAdquiringDate())
        .own(request.isOwn())
        .notes(request.getNotes())
        .image(imageResponse)
        .collection(collectionListResponse)
        .build();
    /*if (imageResponse != null) {
      collectionItem = CollectionItem.builder()
          .name(request.getName())
          .serie(collectionSeriesList)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .image(image)
          .collection(collectionList)
          .build();
      collectionItemsResponse = CollectionItemsResponse.builder()
          .name(request.getName())
          .serie(collectionSeriesListResponse)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .image(imageResponse)
          .collection(collectionListResponse)
          .build();

    } else {
      collectionItem = CollectionItem.builder()
          .name(request.getName())
          .serie(collectionSeriesList)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .collection(collectionList)
          .build();
      collectionItemsResponse = CollectionItemsResponse.builder()
          .name(request.getName())
          .serie(collectionSeriesListResponse)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .collection(collectionListResponse)
          .build();
    }
    this.collectionItemRepository.save(collectionItem);*/

    return collectionItemsResponse;
  }

  public boolean deleteCollectionItem(Long id) throws NotFoundException {
    CollectionItem col = this.collectionItemRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.collectionItemRepository.deleteById(col.getId());
    return true;
  }

  public boolean deleteCollection(Long id) throws NotFoundException {
    CollectionList col = this.collectionListRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.collectionListRepository.deleteById(col.getId());
    return true;
  }

  public List<CollectionSeriesListResponse> listCollections() {
    final List<CollectionSeriesListResponse> collectionListResponseList = new LinkedList<>();
    List<CollectionList> collections = this.collectionListRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionSerieListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionSeriesListResponse> listAllSeriesCollections() {
    final List<CollectionSeriesListResponse> collectionSerieListResponseList = new LinkedList<>();
    List<CollectionSeriesList> collections = this.collectionSeriesListRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionSerieListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionSeriesListResponse> listSeriesByCollection(Long id) {
    final List<CollectionSeriesListResponse> collectionSerieListResponseList = new LinkedList<>();
    List<CollectionSeriesList> collections = this.collectionSeriesListRepository
        .findAllByCollection_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionSerieListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getCollectionById(Long id) {
    final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<CollectionItem> collections = this.collectionItemRepository
        .findByCollection_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemsResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getMoneyFromAllItems() {
    final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<CollectionItem> collections = this.collectionItemRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemsResponse)
        .collect(Collectors.toList());
  }

  private CollectionItemsResponse toCollectionItemsResponse(CollectionItem collection) {
    ImageResponse image = null;
    if (collection.getImage() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getImage().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }

    CollectionSeriesListResponse collectionSeriesListResponse = null;
    try {
      collectionSeriesListResponse = toCollectionSerieListResponse(
          this.collectionSeriesListRepository.findById(collection.getSerie().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    CollectionListResponse collectionListResponse = null;
    try {
      collectionListResponse = toCollectionListResponse(
          this.collectionListRepository.findById(collection.getCollection().getId())
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
        .serie(collectionSeriesListResponse)
        .collection(collectionListResponse)
        .year(collection.getYear())
        .price(collection.getPrice())
        .own(collection.isOwn())
        .wanted(collection.isWanted())
        .notes(collection.getNotes())
        .adquiringDate(collection.getAdquiringDate())
        .metadata(collectionMetadata)
        .build();
  }

  private CollectionSeriesListResponse toCollectionSerieListResponse(
      CollectionSeriesList collection) {
    ImageResponse image = null;
    if (collection.getLogo() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getLogo().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    if (image != null) {
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .logo(image)
          .build();
    } else {
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .build();
    }
  }

  private CollectionItemsResponse toCollectionItemResponse(CollectionItem collection) {
    ImageResponse image = null;
    if (collection.getImage() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getImage().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    try {
      collectionSeriesListResponse = toCollectionSerieListResponse(
          this.collectionSeriesListRepository.findById(collection.getSerie().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    if (image != null) {
      return CollectionItemsResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .image(image)
          .adquiringDate(collection.getAdquiringDate())
          .notes(collection.getNotes())
          .price(collection.getPrice())
          .year(collection.getYear())
          .serie(collectionSeriesListResponse)
          .own(collection.isOwn())
          .wanted(collection.isWanted())
          .build();
    } else {
      return CollectionItemsResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .adquiringDate(collection.getAdquiringDate())
          .notes(collection.getNotes())
          .price(collection.getPrice())
          .year(collection.getYear())
          .serie(collectionSeriesListResponse)
          .own(collection.isOwn())
          .wanted(collection.isWanted())
          .build();
    }

  }

  private CollectionListResponse toCollectionListResponse(CollectionList request) {
    ImageResponse image = null;
    if (request.getLogo() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(request.getLogo().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
      return CollectionListResponse.builder()
          .id(request.getId())
          .name(request.getName())
          .logo(image)
          .build();
    } else {
      return CollectionListResponse.builder()
          .id(request.getId())
          .name(request.getName())
          .build();
    }
  }

  private CollectionSeriesListResponse toCollectionSerieListResponse(
      CollectionList collection) {
    ImageResponse image = null;
    if (collection.getLogo() != null) {
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
    } else {
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .build();
    }
  }

  private CollectionResponse toCollectionResponse(CollectionList request,
      CollectionRequest collectionRequest) {
    return CollectionResponse.builder()
        .id(request.getId())
        .collection(request.getName())
        .logo(request.getLogo())
        .template((collectionRequest.getTemplate()))
        .build();
  }

  private CollectionResponse toCollectionResponse(CollectionList request) {
    return CollectionResponse.builder()
        .collection(request.getName())
        .logo(request.getLogo())
        .build();
  }

  private CollectionMetadataResponse toCollectionMetadataResponse(CollectionMetadata request) {
    return CollectionMetadataResponse.builder()
        .id(request.getId())
        .value(request.getValue())
        .name(request.getName())
        .build();
  }

  private ImageResponse toImageResponse(Image request) {
    return ImageResponse.builder()
        .id(request.getId())
        .name(request.getName())
        .path(request.getPath())
        .created(request.getCreated())
        .build();
  }

}
