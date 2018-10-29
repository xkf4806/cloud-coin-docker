package com.ourdax.coindocker.asset.qtum.qrc20.epc;

import com.ourdax.coindocker.asset.qtum.qrc20.QRC20RpcProcessor;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.QRC20Enum;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@Component(value = "EPCRpcProcessor")
@Lazy
@Slf4j
public class EPCRpcProcessor extends QRC20RpcProcessor {

  @PostConstruct
  public void init(){
    qrc20Enum = QRC20Enum.fromAssetCode(getAssetCode());
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.EPC;
  }
}
