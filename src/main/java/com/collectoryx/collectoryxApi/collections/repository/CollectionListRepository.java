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

  @Query(value = "SELECT COUNT(*) FROM collection c "
      + "JOIN collection_list l ON c.collection=l.id "
      + "JOIN  users u ON l.user_id=u.id "
      + "WHERE c.own=true AND u.id=:userId LIMIT 1 GROUP BY l.name",
      nativeQuery = true)
  long getCompletedCollections(@Param("userId") Long id);
}
