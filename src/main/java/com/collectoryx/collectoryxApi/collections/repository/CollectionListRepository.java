package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionListRepository extends JpaRepository<CollectionList, Long> {

  List<CollectionList> findAll();

  Page<CollectionList> findAllByUser_Id(Long id, Pageable pageable);

  long countByUserId_Id(Long id);

  @Query(value = "SELECT SUM(total_items) from collection_list cl where user_id =:userId ",
      nativeQuery = true)
  Long sumItemsByCollectionUser(@Param("userId") Long id);

  long countByWantedAndUserId_Id(int i, Long id);

  Page<CollectionList> findByNameContaining(String search, Pageable pageable);

  List<CollectionList> findByLogo_Id(Long id);
}
