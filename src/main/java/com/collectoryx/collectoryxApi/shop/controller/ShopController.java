package com.collectoryx.collectoryxApi.shop.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/shop")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class ShopController {

  @GetMapping(value = "/key-request/{email}")
  @PreAuthorize("hasAuthority('USER_ROLE')")
  public Mono<String> getUserKeyLicense(@PathVariable("email") String email,
      @RequestHeader(value = "Authorization") String token) {
    String prueba="hola";
    return Mono.just(prueba);
  }

}
