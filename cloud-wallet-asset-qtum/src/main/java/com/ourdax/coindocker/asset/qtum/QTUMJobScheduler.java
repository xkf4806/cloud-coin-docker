package com.ourdax.coindocker.asset.qtum;

import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Component
@Lazy
public class QTUMJobScheduler extends QTUMBasedJobScheduler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.QTUM;
  }
}
