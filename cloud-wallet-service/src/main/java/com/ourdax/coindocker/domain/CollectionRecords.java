package com.ourdax.coindocker.domain;

import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * Created by zhangjinyang on 2018/7/18.
 */
@Data
public class CollectionRecords {

  private Integer id;

  private String assetCode;

  private String sourceAddr;

  private String targetAddr;

  private String txId;

  private BigDecimal fee;

  private BigDecimal amount;

  private CollectOrderStatus status;

  private CollectType collectType;

  private TargetAddressType targetType;

  private String errorMsg;

  private Date createTime;

  private Date updateTime;

  public enum CollectType{
    UNIFORM_ACCOUNT, FROM_USER

  }

  public enum TargetAddressType{
    TO_COLD_WALLET, TO_HOT_WALLET

  }

}
