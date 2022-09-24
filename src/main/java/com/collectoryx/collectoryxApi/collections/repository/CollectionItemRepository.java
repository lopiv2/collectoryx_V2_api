package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

  //@Query(value = "CREATE TABLE :tableName (id int)", nativeQuery = true)
  //String createTable(@Param("tableName") String tableLookUp);

  //List<CollectionItem> findByCollection_Id(Long collection_id);

  Page<CollectionItem> findByCollection_Id(Long collection_id, Pageable pageable);

  List<CollectionItem> findByCollection_UserId_Id(Long id);

  @Query(value = "SELECT * FROM collection c "
      + "JOIN collection_list l ON c.collection=l.id "
      + "JOIN  users u ON l.user_id=u.id "
      + "WHERE (c.adquiring_date BETWEEN :startDate AND :endDate) AND u.id=:userId",
      nativeQuery = true)
  List<CollectionItem> getItemsPerYear(@Param("userId") Long id,
      @Param("startDate") LocalDate start,
      @Param("endDate") LocalDate end);

  @Query(value = "SELECT * FROM collection c "
      + "JOIN collection_list l ON c.collection=l.id "
      + "JOIN  users u ON l.user_id=u.id "
      + "WHERE c.price=(SELECT MAX(price) FROM collection) AND u.id=:userId LIMIT 1",
      nativeQuery = true)
  CollectionItem getMostValuableItem(@Param("userId") Long id);

  List<CollectionItem> findAllBySerie_Id(Long id);

  Page<CollectionItem> findByCollection_IdAndNameContaining(Long valueOf, String search,
      Pageable pageable);

  Page<CollectionItem> findAllByCollection_UserId_IdOrderByAdquiringDateDesc(Long valueOf,
      Pageable pageable);
}
