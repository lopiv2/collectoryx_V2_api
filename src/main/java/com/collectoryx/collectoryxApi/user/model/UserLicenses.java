package com.collectoryx.collectoryxApi.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "user_licenses")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLicenses {

  @Column
  protected boolean paid = false;
  @Column
  protected boolean trialActivated = false;
  @Enumerated(EnumType.STRING)
  protected LicenseStateTypes state = LicenseStateTypes.Pending;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "licenseCheckMachine")
  protected UserMachines licenseCheckMachine;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String license;
  @Enumerated(EnumType.STRING)
  @Default
  private LicenseTypes type = LicenseTypes.Trial;
  /**
   * Certificate effective time
   */
  @Column
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.TIMESTAMP)
  private Date issuedTime = new Date();
  /**
   * Certificate expiration time
   */
  @Column
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date expiryTime;
  /**
   * customer type
   */
  private String consumerType = "user";

}
