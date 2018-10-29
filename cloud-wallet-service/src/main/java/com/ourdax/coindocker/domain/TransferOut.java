package com.ourdax.coindocker.domain;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author think on 9/1/2018
 */
@Data
public class TransferOut {
  private Integer id;
  private String assetCode;
  private BigDecimal amount;
  private BigDecimal txFee;
  private String fromAccount;
  private String fromCoinAddress;
  private String toCoinAddress;
  private String innerOrderNo;
  private String txId;
  private String assetSymbol;
  private State state;
  private String txNum;
  private String errorMessage;
  private Integer query;
  private Integer confirmNum;
  private WithdrawStatus withdrawStatus;
  private String failMessage;
  private Date createDate;
  private Date updateDate;

  public enum State {
    SUBMIT, RECV, SUCCESS, ERROR;
  }

  public enum WithdrawStatus {
    NEW, CONFIRMING, LONGTIME, CONFIRMED, FAIL;
  }
}
