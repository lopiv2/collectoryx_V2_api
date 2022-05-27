package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionSeriesListRepository extends JpaRepository<CollectionSeriesList,Long> {

}
