package com.collectoryx.collectoryxApi.config.repository;

import com.collectoryx.collectoryxApi.config.model.Config;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {

  List<Config> findAllById(Long id);

  Iterable<Config> findAllByUser_Id(Long id);

  Config findByUser_Id(Long id);
}
