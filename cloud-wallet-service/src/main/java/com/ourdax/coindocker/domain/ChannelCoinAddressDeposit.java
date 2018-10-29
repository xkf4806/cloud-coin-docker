package com.ourdax.coindocker.domain;

import java.util.Date;
import lombok.Data;

@Data
public class ChannelCoinAddressDeposit {

  private Integer id;

  private Integer uid;

  private String assetCode;

  private Integer depositPoolId;

  private String name;

  private String coinAddress;

  private DelFlag delFlag;

  private Date createDate;

  private String createIp;

  enum DelFlag {
    /**
     * 已删除
     */
    TRUE,

    /**
     * 未删除
     */
    FALSE
  }
}