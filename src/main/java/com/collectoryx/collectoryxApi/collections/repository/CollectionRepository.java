package com.collectoryx.collectoryxApi.collections.repository;

import com.collectoryx.collectoryxApi.collections.model.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

  //@Query(value = "CREATE TABLE :tableName (id int)", nativeQuery = true)
  //String createTable(@Param("tableName") String tableLookUp);

  List<Collection> findByCollection_Id(Long collection_id);

}
