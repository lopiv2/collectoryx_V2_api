package com.collectoryx.collectoryxApi.user.repository;

import com.collectoryx.collectoryxApi.user.model.LicenseStateTypes;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLicensesRepository extends JpaRepository<UserLicenses, Long> {


  List<UserLicenses> findByState(LicenseStateTypes pending);

  UserLicenses findByLicenseCheckMachine_User_Email(String email);
}
