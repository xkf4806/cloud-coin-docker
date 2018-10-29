package com.ourdax.coindocker.asset.usdt;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.trans.DefaultTransHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "USDTTransHandler")
public class USDTTransHandler extends DefaultTransHandler {
//  @Autowired
//  private USDTProperties properties;

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.USDT;
  }
}
