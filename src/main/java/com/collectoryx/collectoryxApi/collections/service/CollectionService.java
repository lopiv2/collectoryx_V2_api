package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.images.model.Images;
import com.collectoryx.collectoryxApi.images.repository.ImagesRepository;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final CollectionListRepository collectionListRepository;
  private final ImagesRepository imagesRepository;

  public CollectionService(CollectionRepository collectionRepository,
      CollectionListRepository collectionListRepository,
      ImagesRepository imagesRepository) {
    this.collectionRepository = collectionRepository;
    this.collectionListRepository = collectionListRepository;
    this.imagesRepository=imagesRepository;
  }

  public CollectionResponse createCollection(CollectionRequest collectionRequest) {
    Images image= Images.builder()
        .name(collectionRequest.getName())
        .path(collectionRequest.getLogo().getPath())
        .build();
    CollectionList collectionList = CollectionList.builder()
        .name(collectionRequest.getName())
        .logo(image)
        .build();
    this.imagesRepository.save(image);
    //this.collectionRepository.save(collection);

    this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = toCollectionResponse(collectionList, collectionRequest);
    return collectionResponse;
  }

  private CollectionResponse toCollectionResponse(CollectionList request,CollectionRequest collectionRequest ) {
    return CollectionResponse.builder()
        .collection(request.getName())
        .logo(request.getLogo())
        .template((collectionRequest.getTemplate()))
        .build();
  }

}
