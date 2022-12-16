package com.collectoryx.collectoryxApi.config.repository;

import com.collectoryx.collectoryxApi.config.model.ConfigApiKeys;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigApiKeysRepository extends JpaRepository<ConfigApiKeys, Long> {

  List<ConfigApiKeys> findAllByUserId(Long id);

  ConfigApiKeys findByNameAndUser_Id(String name, Long userId);
}
