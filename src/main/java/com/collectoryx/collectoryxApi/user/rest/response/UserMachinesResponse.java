package com.collectoryx.collectoryxApi.user.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMachinesResponse {

  private String cpuSerial;

  private String moboSerial;

  private UserResponse user_id;

  private List<String> macAddress;

}
