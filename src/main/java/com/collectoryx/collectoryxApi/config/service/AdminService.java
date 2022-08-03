package com.collectoryx.collectoryxApi.config.service;

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
import com.collectoryx.collectoryxApi.user.rest.response.UserResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import org.assertj.core.util.DateUtil;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminService {

  private static SecretKeySpec secretKey;
  private static byte[] key;
  private final UserLicensesRepository userLicensesRepository;
  private final UserRepository userRepository;
  private final UserMachinesRepository userMachinesRepository;

  public AdminService(UserLicensesRepository userLicensesRepository,
      UserRepository userRepository, UserMachinesRepository userMachinesRepository) {
    this.userLicensesRepository = userLicensesRepository;
    this.userRepository = userRepository;
    this.userMachinesRepository = userMachinesRepository;
  }

  public static String insertPeriodically(
      String text, String insert, int period) {
    StringBuilder builder = new StringBuilder(
        text.length() + insert.length() * (text.length() / period) + 1);

    int index = 0;
    String prefix = "";
    while (index < text.length()) {
      // Don't put the insert in the very first iteration.
      // This is easier than appending it *after* each substring
      builder.append(prefix);
      prefix = insert;
      builder.append(text.substring(index,
          Math.min(index + period, text.length())));
      index += period;
    }
    return builder.toString();
  }

  public UserMachinesResponse getMachineByUserId(Long id) {
    UserMachines userMachines = null;
    userMachines = this.userMachinesRepository.findByUser_Id(id);
    return toUserMachinesResponse(userMachines);
  }

  public UserResponse getUserIdByEmail(String email) {
    User user = this.userRepository.findByEmail(email);
    UserResponse userResponse = toUserResponse(user);
    return userResponse;
  }

  public UserLicenseResponse setUserLicenseCode(UserMachinesResponse userMachinesResponse)
      throws Exception {
    String code = getMachineCode(userMachinesResponse);
    Date today = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(today);
    UserLicenseResponse userLicenseResponse = null;
    UserLicenses userLicenses = this.userLicensesRepository
        .findByLicenseCheckMachine_User_Email(userMachinesResponse.getUser_id().getEmail());
    userLicenses.setLicense(code);
    userLicenses.setState(LicenseStateTypes.Activated);
    userLicenses.setIssuedTime(DateUtil.now());
    if (userLicenses.getType() == LicenseTypes.Monthly) {
      cal.add(Calendar.MONTH, 1);
      Date modifiedDate = cal.getTime();
      userLicenses.setExpiryTime(modifiedDate);
    }
    if (userLicenses.getType() == LicenseTypes.Yearly) {
      cal.add(Calendar.YEAR, 1);
      Date modifiedDate = cal.getTime();
      userLicenses.setExpiryTime(modifiedDate);
    }
    if (userLicenses.getType() == LicenseTypes.Trial) {
      cal.add(Calendar.DAY_OF_MONTH, 15);
      Date modifiedDate = cal.getTime();
      userLicenses.setExpiryTime(modifiedDate);
      userLicenses.setTrialActivated(true);
    }
    if (userLicenses.getType() == LicenseTypes.Lifetime) {
      cal.add(Calendar.YEAR, 7000);
      Date modifiedDate = cal.getTime();
      userLicenses.setExpiryTime(modifiedDate);
    }
    userLicenseResponse=toUserLicenseResponse(userLicenses);
    this.userLicensesRepository.save(userLicenses);
    return userLicenseResponse;
  }

  public String getMachineCode(UserMachinesResponse userMachinesResponse) throws Exception {
    Set<String> result = new HashSet<>();
    List<String> mac = userMachinesResponse.getMacAddress();
    result.add(mac.get(0)); //Cambiar en un futuro para que permita añadir varias macs
    String cpuSerial = userMachinesResponse.getCpuSerial();
    result.add(cpuSerial);
    String moboSerial = userMachinesResponse.getMoboSerial();
    result.add(moboSerial);
    Properties props = System.getProperties();
    String javaVersion = props.getProperty("java.version");
    result.add(javaVersion);
    String javaVMVersion = props.getProperty("java.vm.version");
    result.add(javaVMVersion);
    String osVersion = props.getProperty("os.version");
    result.add(osVersion);
    Date grantDate = DateUtil.now();
    result.add(grantDate.toString());

    byte[] msg = result.toString().getBytes();

    byte[] hash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA3-256");
      hash = md.digest(msg);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    StringBuilder strBuilder = new StringBuilder();
    for (byte b : hash) {
      strBuilder.append(String.format("%02x", b));
    }
    String strHash = strBuilder.toString();
    return insertPeriodically(strHash, "-", 4);
  }

  public List<UserLicenseResponse> listPendingLicenses() {
    List<UserLicenses> collections = this.userLicensesRepository
        .findByState(LicenseStateTypes.Pending);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toUserLicenseResponse)
        .collect(Collectors.toList());
  }

  public List<UserLicenseResponse> listAllLicenses() {
    List<UserLicenses> collections = this.userLicensesRepository
        .findAll();
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toUserLicenseResponse)
        .collect(Collectors.toList());
  }

  private UserLicenseResponse toUserLicenseResponse(UserLicenses request) {
    UserMachinesResponse userMachinesResponse = toUserMachinesResponse(
        request.getLicenseCheckMachine());
    return UserLicenseResponse.builder()
        .email(request.getLicenseCheckMachine().getUser().getEmail())
        .machine(userMachinesResponse)
        .state(request.getState())
        .type(request.getType())
        .paid(request.isPaid())
        .trialActivated(request.isTrialActivated())
        .grantedDate(request.getIssuedTime())
        .expiringDate(request.getExpiryTime())
        .licenseCode(request.getLicense())
        .build();
  }

  private UserResponse toUserResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .build();
  }

  public UserMachinesResponse toUserMachinesResponse(UserMachines request) {
    UserResponse userResponse = toUserResponse(request.getUser());
    return UserMachinesResponse.builder()
        .cpuSerial(request.getCpuSerial())
        .moboSerial(request.getMainBoardSerial())
        .user_id(userResponse)
        .macAddress(request.getMacAddress())
        .build();
  }

}
