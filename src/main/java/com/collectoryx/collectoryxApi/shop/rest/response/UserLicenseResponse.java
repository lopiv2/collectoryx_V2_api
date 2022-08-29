package com.collectoryx.collectoryxApi.shop.rest.response;

import com.collectoryx.collectoryxApi.user.model.LicenseStateTypes;
import com.collectoryx.collectoryxApi.user.model.LicenseTypes;
import com.collectoryx.collectoryxApi.user.rest.response.UserMachinesResponse;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLicenseResponse {

  private UserMachinesResponse machine;

  private String email;

  private boolean paid;

  private Date grantedDate;

  private Date expiringDate;

  private Long licenseDuration;

  protected LicenseStateTypes state;

  private LicenseTypes type;

  private String licenseCode;

  private Boolean trialActivated;

}
