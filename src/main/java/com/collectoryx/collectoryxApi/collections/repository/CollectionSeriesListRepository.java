package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionSeriesListRepository extends JpaRepository<CollectionSeriesList, Long> {

  List<CollectionSeriesList> findAll();

  //@Query(value = "SELECT * FROM :tableName (id int)", nativeQuery = true)
  List<CollectionSeriesList> findAllByCollection_Id(Long id);

  List<CollectionSeriesList> findAllByCollection_UserId(Long id);

  CollectionSeriesList findByName(String name);

  List<CollectionSeriesList> findByLogo_Id(Long id);

  List<CollectionSeriesList> findAllByCollection_IdOrderByName(Long id);

  List<CollectionSeriesList> findAllByCollection_UserIdOrderByName(Long id);

  void deleteAllByCollection_Id(Long id);
}
