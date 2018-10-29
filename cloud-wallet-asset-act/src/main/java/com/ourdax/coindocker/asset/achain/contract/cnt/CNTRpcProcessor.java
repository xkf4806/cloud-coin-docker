package com.ourdax.coindocker.asset.achain.contract.cnt;

import com.ourdax.coindocker.asset.achain.contract.ACTCONTRACTRpcProcessor;
import com.ourdax.coindocker.common.enums.AssetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@Component(value = "CNTRpcProcessor")
@Lazy
@Slf4j
public class CNTRpcProcessor extends ACTCONTRACTRpcProcessor {


  @Override
  public AssetCode getAssetCode() {
    return AssetCode.CNT;
  }
}
