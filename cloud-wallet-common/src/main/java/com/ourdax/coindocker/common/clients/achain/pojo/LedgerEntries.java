package com.ourdax.coindocker.common.clients.achain.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
@ToString
public class LedgerEntries {

  private Amount amount;

  private String memo;

  @JsonProperty("from_account")
  private String fromAccount;
  @JsonProperty("to_account")
  private String toAccount;

  @JsonProperty("from_account_name")
  private String fromAccountName;
  @JsonProperty("running_balances")
  private List<String> runningBalances;
  @JsonProperty("to_account_name")
  private String toAccountName;
}
