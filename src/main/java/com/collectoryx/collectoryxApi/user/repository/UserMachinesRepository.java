package com.collectoryx.collectoryxApi.user.repository;

import com.collectoryx.collectoryxApi.user.model.UserMachines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMachinesRepository extends JpaRepository<UserMachines, Long> {

  UserMachines findByUserId_Email(String email);
}
