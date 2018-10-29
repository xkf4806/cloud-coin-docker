package com.ourdax.coindocker.rpc;

import com.ourdax.coindocker.common.enums.AssetCode;
import java.math.BigDecimal;
import lombok.Data;
import lombok.ToString;

/**
 * @author think on 11/1/2018
 */
@Data
@ToString
public class RpcTransRequest {
  private String from;
  private String to;
  private BigDecimal amount;
  /**为合约币预留*/
  private AssetCode assetCode;
}
