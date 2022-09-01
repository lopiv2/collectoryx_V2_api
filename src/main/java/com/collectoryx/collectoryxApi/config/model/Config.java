package com.collectoryx.collectoryxApi.config.model;

import com.collectoryx.collectoryxApi.user.model.Themes;
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
@Table(name="config")
@NoArgsConstructor
public class Config {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private boolean darkTheme;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "theme_id")
  protected Themes theme;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  protected User user;

}
