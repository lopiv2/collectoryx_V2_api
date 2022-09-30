package com.collectoryx.collectoryxApi.user.service;

import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.request.UserRequest;
import com.collectoryx.collectoryxApi.user.rest.response.UserResponse;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

  public UserResponse getUserDetails(Long id)
      throws NotFoundException {
    User user = this.userRepository.findById(id).orElseThrow(NotFoundException::new);
    return toUserResponse(user);
  }

  public UserResponse updateUserDetails(UserRequest request)
      throws NotFoundException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = this.userRepository.findById(request.getId()).orElseThrow(NotFoundException::new);
    user.setEmail(request.getEmail());
    user.setUserName(request.getUserName());
    user.setFirstName(request.getFirstName());
    if (!encoder.matches(request.getPassword(), user.getPassword())) {
      String newPassword = new BCryptPasswordEncoder().encode(request.getPassword());
      user.setPassword(newPassword);
    }
    user.setLastName(request.getLastName());
    this.userRepository.save(user);
    return toUserResponse(user);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  private UserResponse toUserResponse(User request) {
    return UserResponse.builder()
        .id(request.getId())
        .userName(request.getUserName())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .password(request.getPassword())
        .email(request.getEmail())
        .build();
  }

}
