package com.collectoryx.collectoryxApi.user.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
@Table(name = "user_machines")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMachines {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * IP addresses that can be allowed
   */
  @Column
  @ElementCollection(targetClass=String.class)
  private List<String> ipAddress;

  /**
   * Allowable CPU serial number
   */
  @Column
  protected String cpuSerial;

  /**
   * Allowed motherboard serial number
   */
  @Column
  protected String mainBoardSerial;

  /**
   * Allowable MAC address
   */
  @Column
  @ElementCollection(targetClass=String.class)
  private List<String> macAddress;

  /**
   * Machine´s user
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  protected User user;

}
