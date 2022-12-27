package com.collectoryx.collectoryxApi.config.repository;

import com.collectoryx.collectoryxApi.config.model.Connections;
import com.collectoryx.collectoryxApi.config.model.ConnectionsTelegram;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigConnectionTelegramRepository extends JpaRepository<ConnectionsTelegram, Long> {

  ConnectionsTelegram findByConnection_User_Id(Long id);
}
