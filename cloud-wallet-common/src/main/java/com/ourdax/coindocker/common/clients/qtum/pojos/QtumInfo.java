package com.ourdax.coindocker.common.clients.qtum.pojos;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neemre.btcdcli4j.core.domain.Entity;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/4/16.
 */




@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    ignoreUnknown = true
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QtumInfo extends Entity {
  private Integer version;
  @JsonProperty("protocolversion")
  private Integer protocolVersion;
  @JsonProperty("walletversion")
  private Integer walletVersion;
  private BigDecimal balance;
  private Integer stake;
  private Integer blocks;
  @JsonProperty("timeoffset")
  private Integer timeOffset;
  private Integer connections;
  private String proxy;
  private Difficulty difficulty;
  private Boolean testnet;
  @JSONField(name = "moneysupply")
  private Long moneySupply;
  @JsonProperty("keypoololdest")
  private Long keypoolOldest;
  @JsonProperty("keypoolsize")
  private Integer keypoolSize;
  @JsonProperty("unlocked_until")
  private Long unlockedUntil;
  @JsonProperty("paytxfee")
  private BigDecimal payTxFee;
  @JsonProperty("relayfee")
  private BigDecimal relayFee;
  private String errors;

}

