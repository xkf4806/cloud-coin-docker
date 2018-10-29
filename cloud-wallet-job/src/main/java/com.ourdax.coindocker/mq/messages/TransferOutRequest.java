package com.ourdax.coindocker.mq.messages;

import java.math.BigDecimal;
import lombok.Data;
import lombok.ToString;

/**
 * @author think on 11/1/2018
 */
@Data
@ToString
public class TransferOutRequest {
  private String txid;
  private String address;
  private BigDecimal amount;
  private BigDecimal txfee;
  private String message;
}


