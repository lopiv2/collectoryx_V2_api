package com.collectoryx.collectoryxApi.collections.model;

import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.user.model.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "collection_list")
public class CollectionList {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;

  @Column
  protected String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "images_id")
  protected Image logo;

  @Column(columnDefinition = "boolean default false")
  protected Boolean ambit;

  @Enumerated(EnumType.STRING)
  protected CollectionTemplateType template;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  protected User user;

  @Column
  protected Integer owned=0;

  @Column
  protected Integer wanted=0;

  @Column
  protected Integer totalItems=0;

  @Column
  protected float totalPrice= 0;

}
