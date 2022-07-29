package com.collectoryx.collectoryxApi.security.controller;

import com.collectoryx.collectoryxApi.security.rest.request.LoginRequest;
import com.collectoryx.collectoryxApi.security.rest.request.RegisterRequest;
import com.collectoryx.collectoryxApi.security.service.AuthService;
import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.shop.service.ShopService;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.service.JwtUserDetailsService;
import com.collectoryx.collectoryxApi.util.JwtTokenUtil;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AuthenticationController {

  protected final Log logger = LogFactory.getLog(getClass());

  final UserRepository userRepository;
  final AuthenticationManager authenticationManager;
  final JwtUserDetailsService userDetailsService;
  final JwtTokenUtil jwtTokenUtil;
  private final AuthService authService;
  private final ShopService shopService;

  public AuthenticationController(UserRepository userRepository,
      AuthenticationManager authenticationManager,
      JwtUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil,
      AuthService authService, ShopService shopService) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
    this.authService = authService;
    this.shopService = shopService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest request) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUser_name()
              , request.getPassword()));
      if (auth.isAuthenticated()) {
        logger.info("Logged In");
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUser_name());
        System.out.println(userDetails);
        String token = jwtTokenUtil.generateToken(userDetails);
        String role = userDetailsService.getRole(request.getUser_name());
        String email = userDetailsService.getEmail(request.getUser_name());
        Long id = userDetailsService.getId(request.getUser_name());
        String licenseType = userDetailsService.getLicenseType(email);
        responseMap.put("id", id);
        responseMap.put("error", false);
        responseMap.put("message", "Logged In");
        responseMap.put("license", licenseType);
        responseMap.put("token", token);
        responseMap.put("role", role);
        responseMap.put("email", email);
        return ResponseEntity.ok(responseMap);
      } else {
        responseMap.put("error", true);
        responseMap.put("message", "Invalid Credentials");
        return ResponseEntity.status(401).body(responseMap);
      }
    } catch (DisabledException e) {
      e.printStackTrace();
      responseMap.put("error", true);
      responseMap.put("message", "User is disabled");
      return ResponseEntity.status(500).body(responseMap);
    } catch (BadCredentialsException e) {
      responseMap.put("error", true);
      responseMap.put("message", "Invalid Credentials");
      return ResponseEntity.status(401).body(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("error", true);
      responseMap.put("message", "Something went wrong");
      return ResponseEntity.status(500).body(responseMap);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> saveUser(@RequestBody @Valid RegisterRequest request) {
    Map<String, Object> responseMap = new HashMap<>();
    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
    user.setRole("USER");
    user.setUserName(request.getUserName());
    UserDetails userDetails = userDetailsService.createUserDetails(request.getUserName(),
        user.getPassword());
    String token = jwtTokenUtil.generateToken(userDetails);
    //Se genera una licencia trial por defecto al crear un usuario nuevo
    UserLicenseResponse userLicenseResponse = this.shopService.SetClientLicensePetition(
        request.getEmail(),
        "Trial");
    //userRepository.save(user);
    responseMap.put("error", false);
    responseMap.put("username", request.getUserName());
    responseMap.put("message", "Account created successfully");
    responseMap.put("token", token);
    return ResponseEntity.ok(responseMap);
  }

  /*@PostMapping("/register")
  public ResponseEntity<?> saveUser(@RequestBody @Valid RegisterRequest request) {
    Map<String, Object> responseMap = new HashMap<>();
    String hola = this.authService.replicateUserRecordAdminServer(request);
    //System.out.println(hola);
    responseMap.put("hola", hola);
    responseMap.put("error", false);
    responseMap.put("username", request.getUserName());
    responseMap.put("message", "Account created successfully");
    return ResponseEntity.ok(responseMap);
  }*/

}
