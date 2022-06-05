package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import javax.transaction.Transactional;
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

  /*public CollectionResponse createCollection(CollectionRequest collectionRequest) {
    Image image = null;
    image = this.imagesRepository.findImageByName(collectionRequest.getFile().getName())
        .orElse(Image.builder().name(collectionRequest.getFile().getName()).build());
    CollectionList collectionList = CollectionList.builder()
        .name(collectionRequest.getName())
        .logo(image)
        .build();
    //this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = toCollectionResponse(collectionList, collectionRequest);
    return collectionResponse;
  }*/

  public CollectionResponse createCollection(CollectionRequest request) {

    //this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = CollectionResponse.builder().collection(
            request.getName())
        .template(request.getTemplate()).logo(null).build();
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
