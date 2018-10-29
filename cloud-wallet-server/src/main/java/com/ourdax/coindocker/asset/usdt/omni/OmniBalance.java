package com.ourdax.coindocker.asset.usdt.omni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neemre.btcdcli4j.core.common.Defaults;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/3/7.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmniBalance {

  @Setter(AccessLevel.NONE)
  private BigDecimal balance;

  @Setter(AccessLevel.NONE)
  private BigDecimal reserved;

  @Setter(AccessLevel.NONE)
  private BigDecimal frozen;

  public void setBalance(BigDecimal balance) {
    this.balance = balance.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
  }

  public void setReserved(BigDecimal reserved) {
    this.reserved = reserved.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
  }

  public void setFrozen(BigDecimal frozen) {
    this.frozen = frozen.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
  }

}
