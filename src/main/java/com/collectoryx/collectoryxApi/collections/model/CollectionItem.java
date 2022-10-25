package com.collectoryx.collectoryxApi.collections.model;

import com.collectoryx.collectoryxApi.image.model.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@Entity
@Table(name = "collection")
@NoArgsConstructor
public class CollectionItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;

  @Column
  protected String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image")
  protected Image image;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection")
  protected CollectionList collection;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "serie")
  protected CollectionSeriesList serie;

  @Column
  protected Integer year;

  @Column
  protected Float price;

  @Column
  protected boolean own = false;

  @Column
  protected boolean wanted = false;

  @Column
  protected String notes;

  @Column
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.TIMESTAMP)
  private Date acquiringDate;
}
