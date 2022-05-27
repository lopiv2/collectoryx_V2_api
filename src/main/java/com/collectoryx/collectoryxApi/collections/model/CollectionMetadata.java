package com.collectoryx.collectoryxApi.collections.model;

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
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "collection_metadata")
public class CollectionMetadata {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  private String value;

  @Enumerated(EnumType.STRING)
  @Default
  private CollectionMetadataType type = CollectionMetadataType.STRING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection")
  private Collection collection;

}
