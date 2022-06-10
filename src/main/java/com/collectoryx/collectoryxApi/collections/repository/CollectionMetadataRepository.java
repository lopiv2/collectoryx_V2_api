package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionMetadata;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionMetadataRepository extends JpaRepository<CollectionMetadata, Long> {

  List<CollectionMetadata> findByCollection(Long collection_id);

}
