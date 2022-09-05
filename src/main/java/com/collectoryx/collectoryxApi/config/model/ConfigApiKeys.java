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
@Table(name="configApiKeys")
@NoArgsConstructor
public class ConfigApiKeys {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  private String keyCode;

  private String apiLink;

  private String logo;

  private boolean notDelete;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  protected User user;

}
