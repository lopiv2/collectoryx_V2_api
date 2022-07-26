package com.collectoryx.collectoryxApi.config.service;

import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.user.model.LicenseStateTypes;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminService {

  private final UserLicensesRepository userLicensesRepository;

  public AdminService(UserLicensesRepository userLicensesRepository) {
    this.userLicensesRepository = userLicensesRepository;
  }

  public List<UserLicenseResponse> listPendingLicenses() {
    List<UserLicenses> collections = this.userLicensesRepository
        .findByStateContaining(LicenseStateTypes.Pending);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toUserLicenseResponse)
        .collect(Collectors.toList());
  }

  private UserLicenseResponse toUserLicenseResponse(UserLicenses request) {
    return UserLicenseResponse.builder()
        .email(request.getLicenseCheckMachine().getUser().getEmail())
        .machine(request.getLicenseCheckMachine())
        .state(request.getState())
        .type(request.getType())
        .paid(request.isPaid())
        .build();
  }

}
