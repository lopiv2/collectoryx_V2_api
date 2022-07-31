package com.collectoryx.collectoryxApi.user.repository;

import com.collectoryx.collectoryxApi.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "select * from users where user_name = :userName", nativeQuery = true)
  User findUserByUsername(@Param("userName") String userName);

  User findByEmail(String email);

  User findByUserName(String username);
}
