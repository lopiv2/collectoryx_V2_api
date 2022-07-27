package com.collectoryx.collectoryxApi.security.service;

import com.collectoryx.collectoryxApi.security.rest.request.RegisterRequest;
import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import java.time.Duration;
import javax.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

  public void replicateUserRecordAdminServer(RegisterRequest user) {

    webClient.post()
        .uri("/clone-user")
        .body(Mono.just(user), User.class)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(User.class);
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
