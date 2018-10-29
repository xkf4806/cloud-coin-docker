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
public class ContractTransaction {

  @JsonProperty("entry_id")
  private String entryId;
  @JsonProperty("is_virtual")
  private String isVirtual;
  @JsonProperty("fee")
  private ContractFee fee;
  @JsonProperty("ledger_entries")
  private List<ContractLedgerEntries> ledgerEntries;
  @JsonProperty("index")
  private String index;
  @JsonProperty("is_confirmed")
  private Boolean isConfirmed;
  @JsonProperty("block_num")
  private Long blockNum;
  @JsonProperty("received_time")
  private String receivedTime;
  @JsonProperty("is_market")
  private String isMarket;
  @JsonProperty("is_market_cancel")
  private String isMarketCancel;
  @JsonProperty("expiration_timestamp")
  private String expirationTimestamp;
  @JsonProperty("extra_addresses")
  private List<String> extraAddresses;
  @JsonProperty("created_time")
  private String createdTime;
  @JsonProperty("trx")
  private ContractTrx trx;

}
