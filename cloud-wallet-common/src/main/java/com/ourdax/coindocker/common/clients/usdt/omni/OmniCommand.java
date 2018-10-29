package com.ourdax.coindocker.common.clients.usdt.omni;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/3/7.
 */
@Getter
@ToString
@AllArgsConstructor
public enum OmniCommand {

  /**
   * omni
   */
  OMNI_GET_TRANSACTION("omni_gettransaction", 0, 0),
  OMNI_SEND("omni_send", 0, 0),
  OMNI_FUNDED_SEND("omni_funded_send", 0, 0),
  OMNI_GETBALANCE("omni_getbalance", 0, 0),
  OMNI_LISTBLOCKTRANS("omni_listblocktransactions", 0, 0),
  OMNI_CREATEPAYLOAD_SIMPLESEND("omni_createpayload_simplesend", 0, 0),
  OMNI_CREATERAWTX_OPRETURN("omni_createrawtx_opreturn", 0, 0),
  OMNI_CREATERAWTX_REFERENCE("omni_createrawtx_reference", 0, 0),
  OMNI_CREATERAWTX_CHANGE("omni_createrawtx_change", 0, 0);
  private final String name;
  private final int minParams;
  private final int maxParams;
}
