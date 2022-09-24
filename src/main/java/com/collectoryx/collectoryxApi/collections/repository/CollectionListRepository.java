package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionListRepository extends JpaRepository<CollectionList, Long> {

  List<CollectionList> findAll();

  List<CollectionList> findAllByUser_Id(Long id);

  long countByUserId_Id(Long id);

  @Query(value = "SELECT SUM(total_items) from collection_list cl where user_id =:userId ",
      nativeQuery = true)
  long sumItemsByCollectionUser(@Param("userId") Long id);

  long countByWantedAndUserId_Id(int i, Long id);
}
