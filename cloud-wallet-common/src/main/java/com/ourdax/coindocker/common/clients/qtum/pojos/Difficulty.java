package com.ourdax.coindocker.common.clients.qtum.pojos;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/4/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(
    ignoreUnknown = true
)
public class Difficulty {

  @JSONField(name = "proof-of-work")
  private BigDecimal proofOfWork;

  @JSONField(name = "proof-of-stake")
  private BigDecimal proofOfStake;

  public void setProofOfWork(BigDecimal proofOfWork){
    this.proofOfWork = proofOfWork.setScale(16, BigDecimal.ROUND_HALF_UP);
  }

  public void setProofOfStake(BigDecimal proofOfStake){
    this.proofOfStake = proofOfStake.setScale(16, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
  }
}
