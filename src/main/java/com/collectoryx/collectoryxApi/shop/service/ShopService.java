package com.collectoryx.collectoryxApi.shop.service;

import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.user.model.LicenseStateTypes;
import com.collectoryx.collectoryxApi.user.model.LicenseTypes;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.model.UserMachines;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserMachinesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.util.HardwareInfo;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ShopService {

  private final UserRepository userRepository;
  private final UserLicensesRepository userLicensesRepository;
  private final UserMachinesRepository userMachinesRepository;
  private final HardwareInfo hardwareInfo;


  public ShopService(UserRepository userRepository, UserLicensesRepository userLicensesRepository,
      UserMachinesRepository userMachinesRepository, HardwareInfo hardwareInfo) {
    this.userRepository = userRepository;
    this.userLicensesRepository = userLicensesRepository;
    this.userMachinesRepository = userMachinesRepository;
    this.hardwareInfo=hardwareInfo;
  }

  public UserLicenseResponse SetClientLicensePetition(String email, String licenseSelected) {
    User user = this.userRepository.findByEmail(email);
    List<String> Macs = new ArrayList<>();
    Macs.add(HardwareInfo.getMacs());
    UserMachines userMachines = UserMachines.builder()
        .macAddress(Macs)
        .mainBoardSerial(HardwareInfo.getMoBoId())
        .cpuSerial(HardwareInfo.getCPUId())
        //.ipAddress()
        .user(user)
        .build();
    this.userMachinesRepository.save((userMachines));
    UserLicenses userLicenses = UserLicenses.builder()
        .consumerType("user")
        .paid(true)
        .state(LicenseStateTypes.Pending)
        .type(LicenseTypes.valueOf(licenseSelected))
        .licenseCheckMachine(userMachines)
        .build();
    UserLicenseResponse userLicenseResponse = UserLicenseResponse.builder()
        .paid(true)
        .state(LicenseStateTypes.Pending)
        .type(LicenseTypes.valueOf(licenseSelected))
        .email(email)
        .machine(userMachines)
        .build();
    this.userLicensesRepository.save(userLicenses);
    return userLicenseResponse;
  }
}
