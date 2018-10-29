package com.ourdax.coindocker.domain;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author think on 9/1/2018
 */
@Data
public class CollectFeeChargeRecord {
  private Integer id;
  private String assetCode;
  private BigDecimal amount;
  private BigDecimal txFee;
  private String fromAccount;
  private String fromCoinAddress;
  private String toCoinAddress;
  private String txId;
  private TransferStatus transactionStatus;
  private String errorMessage;
  private Integer confirmNum;
  private Date createDate;
  private Date updateDate;
  private Integer collectionId;


  public enum TransferStatus {
    NEW, CONFIRMING, LONGTIME, CONFIRMED, FAIL;
  }
}
