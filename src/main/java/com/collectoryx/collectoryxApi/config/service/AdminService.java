package com.collectoryx.collectoryxApi.config.service;

import com.collectoryx.collectoryxApi.shop.rest.response.UserLicenseResponse;
import com.collectoryx.collectoryxApi.user.model.LicenseStateTypes;
import com.collectoryx.collectoryxApi.user.model.UserLicenses;
import com.collectoryx.collectoryxApi.user.model.UserMachines;
import com.collectoryx.collectoryxApi.user.repository.UserLicensesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserMachinesRepository;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import com.collectoryx.collectoryxApi.user.rest.response.UserMachinesResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
    try {
      userMachines = this.userMachinesRepository
          .findById(id).orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    return toUserMachinesResponse(userMachines);
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

    byte[] msg = result.toString().getBytes();

    byte[] hash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      hash = md.digest(msg);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    StringBuilder strBuilder = new StringBuilder();
    for (byte b : hash) {
      strBuilder.append(String.format("%02x", b));
    }
    String strHash = strBuilder.toString();
    System.out.println("The MD5 hash: " + strHash);

    return insertPeriodically(strHash, "-", 4);

  }

  public List<UserLicenseResponse> listPendingLicenses() {
    List<UserLicenses> collections = this.userLicensesRepository
        .findByState(LicenseStateTypes.Pending);
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
        .build();
  }

  public UserMachinesResponse toUserMachinesResponse(UserMachines request) {
    return UserMachinesResponse.builder()
        .cpuSerial(request.getCpuSerial())
        .moboSerial(request.getMainBoardSerial())
        .user_id(request.getUser())
        .macAddress(request.getMacAddress())
        .build();
  }

}
