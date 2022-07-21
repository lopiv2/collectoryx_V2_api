package com.collectoryx.collectoryxApi.config.controller;

import com.collectoryx.collectoryxApi.config.service.AdminService;
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

  @GetMapping(value = "/keygen/{email}")
  @PreAuthorize("hasRole('ADMIN_ROLE')")
  public Mono<String> getKey(@PathVariable("email") String email,
      @RequestHeader(value = "Authorization") String token) {
    String prueba="";
    return Mono.just(prueba);
  }

}
