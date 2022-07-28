package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionListRepository extends JpaRepository<CollectionList, Long> {

  List<CollectionList> findAll();

  List<CollectionList> findAllByUser_Id(Long id);
}
