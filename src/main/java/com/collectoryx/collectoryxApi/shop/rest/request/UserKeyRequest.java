package com.collectoryx.collectoryxApi.shop.rest.request;

import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserKeyRequest {

  @Email
  private String email;

  private String licenseSelected;

}
