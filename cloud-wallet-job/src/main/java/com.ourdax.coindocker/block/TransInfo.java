package com.ourdax.coindocker.block;

import com.ourdax.coindocker.common.enums.AssetCode;
import java.math.BigDecimal;
import lombok.ToString;

/**
 * @author think on 11/1/2018
 */

@ToString
public class TransInfo implements Trans, Block {
  private String blockNumber;
  private String blockHash;
  private String txId;
  private BigDecimal amount;
  private String from;
  private String to;
  private String vout;
  private Integer confirmNum;
  private Direction direction;
  /**为合约币指定币种*/
  private AssetCode assetCode;
  private BigDecimal fee;

  @Override
  public String getTxId() {
    return txId;
  }

  public String getBlockNumber() {
    return blockNumber;
  }

  public void setBlockNumber(String blockNumber) {
    this.blockNumber = blockNumber;
  }

  public void setBlockHash(String blockHash) {
    this.blockHash = blockHash;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }


  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }


  public String getBlockHash() {
    return blockHash;
  }


  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public String getFrom() {
    return from;
  }

  @Override
  public String getTo() {
    return to;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public Integer getConfirmNum() {
    return confirmNum;
  }

  public void setConfirmNum(Integer confirmNum) {
    this.confirmNum = confirmNum;
  }

  public String getVout() {
    return vout;
  }

  public void setVout(String vout) {
    this.vout = vout;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public enum Direction {
    IN, OUT
  }


  public AssetCode getAssetCode() {
    return assetCode;
  }

  public void setAssetCode(AssetCode assetCode) {
    this.assetCode = assetCode;
  }

  public BigDecimal getFee() {
    return fee;
  }

  public void setFee(BigDecimal fee) {
    this.fee = fee;
  }
}
