package com.ourdax.coindocker.mq.messages;

import java.math.BigDecimal;
import lombok.Data;
import lombok.ToString;

/**
 * @author think on 10/1/2018
 */
@Data
@ToString
public class TransferResult {
  private String assetCode;
  private String orderId;
  private String toWallet;
  private String fromWallet;
  private String sendMessage;
  private BigDecimal amount;
  private String assetStatus;
  private String txId;
  private String innerOrderId;
}
