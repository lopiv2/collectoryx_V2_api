package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
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

  public JwtUserDetailsService(UserRepository userRepository,
      UserLicensesRepository userLicensesRepository) {
    this.userRepository = userRepository;
    this.userLicensesRepository = userLicensesRepository;
  }

  public String getRole(String userName) {
    User user = userRepository.findUserByUsername(userName);
    if (user.getRole().contains("ADMIN")) {
      return "ADMIN_ROLE";
    } else {
      return "USER_ROLE";
    }
  }

  public String getEmail(String userName) {
    User user = userRepository.findUserByUsername(userName);
    return user.getEmail();
  }

  public Long getId(String userName) {
    User user = userRepository.findUserByUsername(userName);
    return user.getId();
  }

  public String getLicenseType(String email) {
    UserLicenses userLicenses = this.userLicensesRepository
        .findByLicenseCheckMachine_User_Email(email);
    return userLicenses.getType().toString();
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
