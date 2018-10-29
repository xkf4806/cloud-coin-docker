package com.ourdax.coindocker.asset.qtum.qrc20;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.trans.DefaultTransHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "QRC20TransHandler")
@Slf4j
public class QRC20TransHandler extends DefaultTransHandler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.QRC20;
  }

}
