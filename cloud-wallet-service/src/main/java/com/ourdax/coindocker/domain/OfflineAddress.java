package com.ourdax.coindocker.domain;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * Created by zhangjinyang on 2018/7/19.
 */
@Data
public class OfflineAddress {

  private Integer id;

  private String assetCode;

  private String address;

  private BigDecimal amount;

  private BigDecimal maxAmount;

  private OfflineAddrStatus status;

  private Date createTime;

  private Date updateTime;

  public enum OfflineAddrStatus {

    INUSE, UNASSIGN, QUIT
  }

}
