package com.collectoryx.collectoryxApi.shop.controller;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.shop.rest.request.UserKeyRequest;
import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.shop.service.ShopService;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @PostMapping(value = "/key-request")
  @PreAuthorize("hasAuthority('USER_ROLE')")
  public Mono<UserLicenseResponse> getUserKeyLicense(@RequestBody @Valid UserKeyRequest request,
      @RequestHeader(value = "Authorization") String token) {
    UserLicenseResponse userLicenseResponse = this.shopService.SetClientLicensePetition(
        request.getEmail(),
        request.getLicenseSelected());
    return Mono.just(userLicenseResponse);
  }

}
