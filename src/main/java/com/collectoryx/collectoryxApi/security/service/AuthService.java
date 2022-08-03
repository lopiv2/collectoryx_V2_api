package com.collectoryx.collectoryxApi.security.service;

import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
public class AuthService {

  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
  private final UserLicensesRepository userLicensesRepository;
  public WebClient webClient = WebClient.builder()
      .baseUrl("http://localhost:8083")
      .build();

  public AuthService(UserLicensesRepository userLicensesRepository) {
    this.userLicensesRepository = userLicensesRepository;
  }

  public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
    if (dateToConvert != null) {
      return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime();
    }
    return null;
  }

  /*private UserLicenseResponse toUserLicenseResponse(UserLicenses request) {
    return UserLicenseResponse.builder()
        .email(request.getLicenseCheckMachine().getUser().getEmail())
        .machine(request.getLicenseCheckMachine())
        .state(request.getState())
        .type(request.getType())
        .paid(request.isPaid())
        .build();
  }*/

}
