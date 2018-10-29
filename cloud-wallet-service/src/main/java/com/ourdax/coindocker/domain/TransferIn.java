package com.ourdax.coindocker.domain;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * @author think on 9/1/2018
 */
@Data
@ToString
public class TransferIn {
  private Integer id;
  private String assetCode;
  private String txId;
  private String blockNum;
  private String blockhash;
  private String fromCoinAddress;
  private String toCoinAddress;
  private String vout;
  private String category;
  private State state;
  private BigDecimal amount;
  private BigDecimal fee;
  private String assetId;
  private String inAddress;
  private Integer confirmNum;
  private DepositStatus depositStatus;
  private String failMessage;
  private Date createDate;
  private Date updateDate;

  public enum State {
    SEND, UNSEND
  }

  public enum DepositStatus {
    NEW, CONFIRMING, LONGTIME, CONFIRMED, FAIL;
  }

}
