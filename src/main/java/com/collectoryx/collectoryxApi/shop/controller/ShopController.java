package com.collectoryx.collectoryxApi.shop.controller;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.shop.service.ShopService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/shop")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class ShopController {

  private final AdminService adminService;
  private final ShopService shopService;

  public ShopController(AdminService adminService, ShopService shopService) {
    this.adminService = adminService;
    this.shopService = shopService;

  }

  @GetMapping(value = "/key-request/{email}")
  @PreAuthorize("hasAuthority('USER_ROLE')")
  public Mono<UserLicenseResponse> getUserKeyLicense(@PathVariable("email") String email,
      @RequestParam("license") String licenseSelected,
      @RequestHeader(value = "Authorization") String token) {
    UserLicenseResponse userLicenseResponse = this.shopService.SetClientLicensePetition(email,
        licenseSelected);
    return Mono.just(userLicenseResponse);
  }

}
