package com.ourdax.coindocker.asset.gto;

import com.ourdax.coindocker.asset.EthContractTransHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 14/1/2018
 */
@Component(value = "GTOTransHandler")
@Lazy
public class GTOTransHandler extends EthContractTransHandler {
  @Override
  public AssetCode getAssetCode() {
    return AssetCode.GTO;
  }
}
