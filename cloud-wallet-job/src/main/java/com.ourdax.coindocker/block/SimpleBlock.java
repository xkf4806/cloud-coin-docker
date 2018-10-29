package com.ourdax.coindocker.block;

import lombok.ToString;

/**
 * @author think on 11/1/2018
 */

@ToString
public class SimpleBlock implements Block {
  private String blockNumber;
  private String blockHash;

  public SimpleBlock(String blockNumber, String blockHash) {
    this.blockNumber = blockNumber;
    this.blockHash = blockHash;
  }

  public SimpleBlock() {
  }


  @Override
  public String getBlockNumber() {
    return blockNumber;
  }

  @Override
  public String getBlockHash() {
    return blockHash;
  }
}
