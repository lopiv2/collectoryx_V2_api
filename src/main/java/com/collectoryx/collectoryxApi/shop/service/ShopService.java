package com.collectoryx.collectoryxApi.shop.service;

import com.collectoryx.collectoryxApi.config.service.AdminService;
import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.user.model.LicenseStateTypes;
import com.collectoryx.collectoryxApi.user.model.LicenseTypes;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.model.UserMachines;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserMachinesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.response.UserMachinesResponse;
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
  private final AdminService adminService;


  public ShopService(UserRepository userRepository, UserLicensesRepository userLicensesRepository,
      UserMachinesRepository userMachinesRepository, HardwareInfo hardwareInfo,
      AdminService adminService) {
    this.userRepository = userRepository;
    this.userLicensesRepository = userLicensesRepository;
    this.userMachinesRepository = userMachinesRepository;
    this.hardwareInfo = hardwareInfo;
    this.adminService = adminService;
  }

  public UserLicenseResponse SetClientLicensePetition(String email, String licenseSelected) {
    User user = this.userRepository.findByEmail(email);
    UserMachines userMachines = null;
    UserMachinesResponse userMachinesResponse = null;
    List<String> Macs = new ArrayList<>();
    Macs.add(HardwareInfo.getMacs());
    //Buscamos si existe la licencia en la BBDD primero y sino generamos la peticion
    UserLicenses userLicenses = this.userLicensesRepository
        .findByLicenseCheckMachine_User_Email(email);
    //Si no existe la peticion de licencia, la creamos
    if (userLicenses == null) {
      //Generamos datos de la maquina
      userMachines = UserMachines.builder()
          .macAddress(Macs)
          .mainBoardSerial(HardwareInfo.getMoBoId())
          .cpuSerial(HardwareInfo.getCPUId())
          //.ipAddress()
          .user(user)
          .build();
      this.userMachinesRepository.save((userMachines));
      //Generamos los datos de la peticion de licencia
      userLicenses = UserLicenses.builder()
          .consumerType("user")
          .paid(true)
          .state(LicenseStateTypes.Pending)
          .type(LicenseTypes.valueOf(licenseSelected))
          .licenseCheckMachine(userMachines)
          .build();
      this.userLicensesRepository.save(userLicenses);
    } else {
      userMachines = this.userMachinesRepository.findByUserId_Email(email);
      userMachinesResponse = this.adminService.toUserMachinesResponse(
          userMachines);
      if (!licenseSelected.contains(userLicenses.getType().toString())) {
        //Solo actualizamos el tipo de licencia solicitado cuando cambia el tipo de licencia
        //sino cambia, no se actualiza
        userLicenses.setType(LicenseTypes.valueOf(licenseSelected));
        this.userLicensesRepository.save(userLicenses);
      }
    }
    UserLicenseResponse userLicenseResponse = new UserLicenseResponse();
    userLicenseResponse.setPaid(true);
    userLicenseResponse.setState(LicenseStateTypes.Pending);
    userLicenseResponse.setType(LicenseTypes.valueOf(licenseSelected));
    userLicenseResponse.setEmail(email);
    userLicenseResponse.setMachine(userMachinesResponse);
    //Si se activa la licencia trial, se hace solo una vez
    if (licenseSelected.equals(LicenseTypes.Trial.toString())) {
      userLicenseResponse.setTrialActivated(true);
    }

    return userLicenseResponse;
  }
}
