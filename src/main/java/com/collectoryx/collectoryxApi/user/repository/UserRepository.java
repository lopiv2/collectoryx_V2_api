package com.collectoryx.collectoryxApi.user.repository;

import com.collectoryx.collectoryxApi.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  //@Query(value = "select * from users where user_name = :user_name", nativeQuery = true)
  //User findUserByUsername(@Param("user_name") String username);

  User findByUsername(String username);
  Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);

}
