package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createClient(String email, String password) {
    User user = com.collectoryx.collectoryxApi.user.model.User.builder().email(email)
        .password((password)).build();
    user = this.userRepository.save(user);
    return user;
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

}
