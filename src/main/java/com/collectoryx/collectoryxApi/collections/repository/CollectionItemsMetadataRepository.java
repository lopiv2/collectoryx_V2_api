package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionItemsMetadata;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionItemsMetadataRepository extends
    JpaRepository<CollectionItemsMetadata, Long> {

  List<CollectionItemsMetadata> findByItem_Id(Long id);

  Optional<Object> findById(String id);

  //CollectionItemsMetadata findByItem(Long id);

  @Query(value = "SELECT * FROM collection_items_metadata c "
      + "JOIN collection l ON c.item_id=l.id "
      + "WHERE c.item_id=:id LIMIT 1",
      nativeQuery = true)
  CollectionItemsMetadata findByItem(Long id);
}
