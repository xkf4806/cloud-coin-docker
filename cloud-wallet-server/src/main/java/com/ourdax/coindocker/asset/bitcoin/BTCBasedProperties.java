package com.ourdax.coindocker.asset.bitcoin;

import lombok.Data;

/**
 * @author think on 9/2/2018
 */
@Data
public class BTCBasedProperties {
  private String rpcHost;
  private Integer rpcPort;
  private String rpcUser;
  private String rpcPassword;
  private String depositAccount;
  private Integer confirmThreshold;
  private Integer batchSize;
  private Integer batchTimeLimitInMins;
}
