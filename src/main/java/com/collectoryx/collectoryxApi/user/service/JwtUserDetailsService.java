package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.config.model.Config;
import com.collectoryx.collectoryxApi.config.repository.ConfigRepository;
import com.collectoryx.collectoryxApi.user.model.LicenseTypes;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.response.ThemeResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  final UserRepository userRepository;
  final UserLicensesRepository userLicensesRepository;
  final UserThemesService userThemesService;
  final ConfigRepository configRepository;

  public JwtUserDetailsService(UserRepository userRepository,
      UserLicensesRepository userLicensesRepository, ConfigRepository configRepository, UserThemesService userThemesService) {
    this.userRepository = userRepository;
    this.userLicensesRepository = userLicensesRepository;
    this.configRepository=configRepository;
    this.userThemesService = userThemesService;
  }

  public String getRole(String userName) {
    User user = userRepository.findByUserName(userName);
    if (user.getRole().contains("ADMIN")) {
      return "ADMIN_ROLE";
    } else {
      return "USER_ROLE";
    }
  }

  public String getEmail(String userName) {
    User user = userRepository.findByUserName(userName);
    return user.getEmail();
  }

  public Long getId(String userName) {
    User user = userRepository.findByUserName(userName);
    return user.getId();
  }

  public ThemeResponse getTheme(String userName) {
    //User user = userRepository.findByUserName(userName);
    Config config=configRepository.findByUser_UserName(userName);
    return userThemesService.toThemesResponse(config.getTheme());
  }

  public UserLicenses getLicenseType(String email) {
    UserLicenses userLicenses = this.userLicensesRepository
        .findByLicenseCheckMachine_User_Email(email);
    return userLicenses;
  }

  public void setTrialActivated(String email) {
    UserLicenses userLicenses = this.userLicensesRepository
        .findByLicenseCheckMachine_User_Email(email);
    userLicenses.setType(LicenseTypes.Free);
    userLicenses.setTrialActivated(true);
    this.userLicensesRepository.save(userLicenses);
  }

  public void setFreeLicense(String email) {
    UserLicenses userLicenses = this.userLicensesRepository
        .findByLicenseCheckMachine_User_Email(email);
    userLicenses.setType(LicenseTypes.Free);
    this.userLicensesRepository.save(userLicenses);
  }

  public Boolean checkUserEmailExists(String email) {
    User user = this.userRepository.findByEmail(email);
    if (user == null) {
      return false;
    } else {
      return true;
    }
  }

  public Boolean checkUserExists(String username) {
    User user = this.userRepository.findByUserName(username);
    if (user == null) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findUserByUsername(username);
    List<GrantedAuthority> authorityList = new ArrayList<>();
    if (user.getRole().contains("ADMIN")) {
      authorityList.add(new SimpleGrantedAuthority("ADMIN_ROLE"));
    } else {
      authorityList.add(new SimpleGrantedAuthority("USER_ROLE"));
    }
    return new org.springframework.security.core.userdetails.User(user.getUserName(),
        user.getPassword(), authorityList);
  }

  public UserDetails createUserDetails(String username, String password) {
    List<GrantedAuthority> authorityList = new ArrayList<>();
    authorityList.add(new SimpleGrantedAuthority("USER_ROLE"));
    return new org.springframework.security.core.userdetails.User(username, password,
        authorityList);
  }
}
