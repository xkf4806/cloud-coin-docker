package com.ourdax.coindocker.asset.usdt.omni;

import com.neemre.btcdcli4j.core.Commands;
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

  /** omni */
  OMNI_GET_TRANSACTION("omni_gettransaction", 0, 0),
  OMNI_SEND("omni_send", 0, 0),
  OMNI_GETBALANCE("omni_getbalance",0,0),
  OMNI_LISTBLOCKTRANS("omni_listblocktransactions",0,0);
  private final String name;
  private final int minParams;
  private final int maxParams;
}
