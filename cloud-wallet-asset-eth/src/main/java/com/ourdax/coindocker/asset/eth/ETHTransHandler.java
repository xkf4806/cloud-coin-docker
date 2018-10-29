package com.ourdax.coindocker.asset.eth;

import com.ourdax.coindocker.asset.EthContractTransHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.trans.DefaultTransHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 14/1/2018
 */
@Component(value = "ETHTransHandler")
@Lazy
public class ETHTransHandler extends DefaultTransHandler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ETH;
  }
}
