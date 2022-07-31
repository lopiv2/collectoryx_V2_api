package com.collectoryx.collectoryxApi.config.controller;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.user.rest.response.UserMachinesResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AdminController {

  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping(value = "/keygen/{id}")
  @PreAuthorize("hasAuthority('ADMIN_ROLE')")
  public Mono<String> generateClientKey(@PathVariable("id") Long userId,
      @RequestHeader(value = "Authorization") String token) throws Exception {
    UserMachinesResponse userMachinesResponse = this.adminService.getMachineByUserId(userId);
    String prueba = this.adminService.getMachineCode(userMachinesResponse);
    return Mono.just(prueba);
  }

  @GetMapping(value = "/get-pending-licenses")
  public Mono<List<UserLicenseResponse>> getPendingLicenses(
      @RequestHeader(value = "Authorization") String token) {
    List<UserLicenseResponse> userLicenseResponses =
        this.adminService.listPendingLicenses();
    return Mono.just(userLicenseResponses);
  }

  @GetMapping(value = "/get-all-licenses")
  public Mono<List<UserLicenseResponse>> getAllLicenses(
      @RequestHeader(value = "Authorization") String token) {
    List<UserLicenseResponse> userLicenseResponses =
        this.adminService.listAllLicenses();
    return Mono.just(userLicenseResponses);
  }

}
