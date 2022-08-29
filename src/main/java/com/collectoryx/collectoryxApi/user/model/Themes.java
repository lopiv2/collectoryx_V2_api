package com.collectoryx.collectoryxApi.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "themes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Themes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(nullable = false, length = 64)
  private String mode;

  @Column(nullable = false, length = 64)
  private String topBarColor;

  @Column(nullable = false, length = 64)
  private String primaryTextColor;

  @Column(nullable = false, length = 64)
  private String secondaryTextColor;

  @Column(nullable = false, length = 64)
  private String listItemColor;

  @Column(nullable = false, length = 64)
  private String sideBarColor;

  @Column(nullable = true)
  private String backgroundImage="";

  @Column(nullable = false, length = 64)
  private String backgroundColor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  protected User user;


}
