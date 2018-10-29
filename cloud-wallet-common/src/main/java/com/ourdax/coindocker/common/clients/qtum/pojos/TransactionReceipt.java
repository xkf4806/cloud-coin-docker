package com.ourdax.coindocker.common.clients.qtum.pojos;

import java.util.List;
import lombok.Data;

/**
 * Created by zhangjinyang on 2018/5/5.
 */
@Data
public class TransactionReceipt {

  private String blockHash;
  private Integer blockNumber;
  private String transactionHash;
  private String from;
  private String to;
  /**
   * 累计使用的gas数量
   */
  private Long cumulativeGasUsed;
  /**
   * 使用的gas数量
   */
  private Long gasUsed;
  private String contractAddress;
  private List<SearchLog> log;


}
