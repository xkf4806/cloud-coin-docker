package com.ourdax.coindocker.rpc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * @author think on 22/1/2018
 */
@Data
@ToString
public class RpcBatchTransferRequest {
  private List<RpcTransRequest> batchRequests;
  private BigDecimal fee;
}
