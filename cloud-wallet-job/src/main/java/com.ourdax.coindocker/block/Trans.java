package com.ourdax.coindocker.block;

import java.math.BigDecimal;

/**
 * @author think on 12/1/2018
 */
public interface Trans {
  BigDecimal getAmount();

  String getTxId();

  String getFrom();

  String getTo();

  String getBlockHash();

  String getBlockNumber();
}
