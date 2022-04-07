package com.collectoryx.collectoryxApi;

import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseTestController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @AfterEach
  public void cleanDatabase() {
    userRepository.deleteAll();
  }

  protected User createUser() {
    User user = com.collectoryx.collectoryxApi.user.model.User.builder()
        .userName("lopiv2")
        .email("lopiv2@hotmail.com")
        .password(("1234"))
        .build();
    user = this.userRepository.save(user);
    return user;
  }

}
