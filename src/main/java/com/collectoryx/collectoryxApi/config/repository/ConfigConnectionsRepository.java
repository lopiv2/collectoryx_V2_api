package com.collectoryx.collectoryxApi.config.repository;

import com.collectoryx.collectoryxApi.config.model.Config;
import com.collectoryx.collectoryxApi.config.model.Connections;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigConnectionsRepository extends JpaRepository<Connections, Long> {

  List<Connections> findAllByUserId(Long id);

  Connections findByNameAndUser_Id(String name, Long userId);
}
