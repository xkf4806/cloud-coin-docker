package com.ourdax.coindocker.common.clients.usdt.omni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neemre.btcdcli4j.core.common.Defaults;
import com.neemre.btcdcli4j.core.domain.OutputOverview;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/9/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmniOutput extends OutputOverview {

  private String address;
  private String account;
  private String scriptPubKey;
  private String redeemScript;
  @Setter(AccessLevel.NONE)
  private BigDecimal value;
  private Integer confirmations;
  private Boolean spendable;


  public OmniOutput(String txId, Integer vOut, String scriptPubKey, String redeemScript, BigDecimal amount) {
    super(txId, vOut);
    this.scriptPubKey = scriptPubKey;
    this.redeemScript = redeemScript;
    this.value = amount;
  }

  public void setAmount(BigDecimal amount) {
    this.value = amount.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
  }
}
