package com.collectoryx.collectoryxApi.util;

import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

@Component
public class HardwareInfo {

  private static String OS = System.getProperty("os.name").toLowerCase();
  private static SystemInfo si = new SystemInfo();
  private static HardwareAbstractionLayer hardware = si.getHardware();
  private static CentralProcessor processor = hardware.getProcessor();
  private static CentralProcessor.ProcessorIdentifier processorIdentifier =
      processor.getProcessorIdentifier();
  private static ComputerSystem computerSystem=hardware.getComputerSystem();
  private static Baseboard motherboard=computerSystem.getBaseboard();
  private static NetworkIF[] networkIFS=hardware.getNetworkIFs().toArray(new NetworkIF[0]);

  public static String getCPUId() {
    return processorIdentifier.getProcessorID();
  }

  public static String getMoBoId() {
    return motherboard.getSerialNumber();
  }

  public static String getMacs(){
    for (NetworkIF net : networkIFS){
      return net.getMacaddr();
    }
    return null;
  }

}
