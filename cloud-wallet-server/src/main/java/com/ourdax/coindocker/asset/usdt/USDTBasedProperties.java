package com.ourdax.coindocker.asset.usdt;

import lombok.Data;

/**
 * @author think on 9/2/2018
 */
@Data
public class USDTBasedProperties {
  private String rpcHost;
  private Integer rpcPort;
  private String rpcUser;
  private String rpcPassword;
  private String depositAccount;
  private Integer confirmThreshold;
  private String depositAddress;
  private String feeAddress;
}
