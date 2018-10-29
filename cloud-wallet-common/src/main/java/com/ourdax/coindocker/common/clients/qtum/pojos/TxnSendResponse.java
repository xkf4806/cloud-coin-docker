package com.ourdax.coindocker.common.clients.qtum.pojos;

import lombok.Data;

/**
 * Created by zhangjinyang on 2018/5/7.
 */
@Data
public class TxnSendResponse {

  private TxnSendResult result;
  private String error;
  private String id;

  @Data
  public class TxnSendResult{
    private String txid;
    private String sender;
    private String hash160;
  }
}
