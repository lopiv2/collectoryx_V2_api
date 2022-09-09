package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionItemsMetadata;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionItemsMetadataRepository extends JpaRepository<CollectionItemsMetadata, Long> {

  List<CollectionItemsMetadata> findByItem_Id(Long id);

  Optional<CollectionItemsMetadata> findById(String id);
}