package com.collectoryx.collectoryxApi.config.model;

import com.collectoryx.collectoryxApi.user.model.User;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@Entity
@Table(name="connections_telegram")
@NoArgsConstructor
public class ConnectionsTelegram {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  private String botToken;

  private String chatId;

  private boolean sentNotifications=false;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_id")
  protected Connections connection;

}
