package com.ourdax.coindocker.domain;

import java.util.Date;
import lombok.Data;

@Data
public class AddressPool {

  private Integer id;

  private String assetCode;

  private String coinAddress;

  private AddressStatus addressStatus;

  private DelFlag delFlag;

  private Date createDate;

  private Date updateDate;

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

  public enum AddressStatus{
    /**
     * 新建
     */
    NEW,
    /**
     * 已分配
     */
    USED;
  }
}