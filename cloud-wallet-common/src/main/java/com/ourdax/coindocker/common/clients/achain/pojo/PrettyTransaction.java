package com.ourdax.coindocker.common.clients.achain.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/2/1.
 */
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
@ToString
public class PrettyTransaction {

  @JsonProperty("is_virtual")
  private String isVirtual;
  @JsonProperty("fee")
  private Fee fee;
  @JsonProperty("ledger_entries")
  private List<LedgerEntries> ledgerEntries;
  @JsonProperty("is_confirmed")
  private String isConfirmed;
  @JsonProperty("block_num")
  private Long blockNum;
  @JsonProperty("is_market")
  private String isMarket;
  @JsonProperty("is_market_cancel")
  private String isMarketCancel;
  @JsonProperty("expiration_timestamp")
  private String expirationTimestamp;
  private String timestamp;
  @JsonProperty("block_position")
  private String blockPosition;
  @JsonProperty("trx_id")
  private String trxId;
  @JsonProperty("trx_type")
  private String trxType;

}
