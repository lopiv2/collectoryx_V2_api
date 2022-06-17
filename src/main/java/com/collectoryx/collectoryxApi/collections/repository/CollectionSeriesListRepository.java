package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionSeriesListRepository extends JpaRepository<CollectionSeriesList, Long> {

  List<CollectionSeriesList> findAll();

}
