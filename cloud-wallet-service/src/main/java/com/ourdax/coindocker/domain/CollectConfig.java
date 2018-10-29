package com.ourdax.coindocker.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CollectConfig {

  private Integer id;

  private String assetCode;

  private BigDecimal lowerLimit;

  private BigDecimal upperLimit;

  private BigDecimal uniformAccountMin;

  private BigDecimal uniformAccountMax;


}