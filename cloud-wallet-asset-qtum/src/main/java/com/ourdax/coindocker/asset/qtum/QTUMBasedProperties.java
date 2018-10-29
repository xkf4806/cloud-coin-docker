package com.ourdax.coindocker.asset.qtum;

import lombok.Data;

/**
 * @author think on 9/2/2018
 */
@Data
public class QTUMBasedProperties {

  private String rpcHost;
  private Integer rpcPort;
  private String rpcUser;
  private String rpcPassword;
  private String depositAccount;
  private Integer confirmThreshold;
  private String qrc20DepositAddress;
  private String qrc20HexDepositAddress;


}
