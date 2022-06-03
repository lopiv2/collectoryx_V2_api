package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.images.model.Image;
import com.collectoryx.collectoryxApi.images.repository.ImageRepository;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final CollectionListRepository collectionListRepository;
  private final ImageRepository imagesRepository;

  public CollectionService(CollectionRepository collectionRepository,
      CollectionListRepository collectionListRepository,
      ImageRepository imagesRepository) {
    this.collectionRepository = collectionRepository;
    this.collectionListRepository = collectionListRepository;
    this.imagesRepository = imagesRepository;
  }

  public CollectionResponse createCollection(CollectionRequest collectionRequest)
      throws NotFoundException {
    Image image = this.imagesRepository.findImageByName(collectionRequest.getName()).orElseThrow(
        NotFoundException::new);
    CollectionList collectionList = CollectionList.builder()
        .name(collectionRequest.getName())
        .logo(image)
        .build();
    //this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = toCollectionResponse(collectionList, collectionRequest);
    return collectionResponse;
  }

  private CollectionResponse toCollectionResponse(CollectionList request,
      CollectionRequest collectionRequest) {
    return CollectionResponse.builder()
        .collection(request.getName())
        .logo(request.getLogo())
        .template((collectionRequest.getTemplate()))
        .build();
  }

}
