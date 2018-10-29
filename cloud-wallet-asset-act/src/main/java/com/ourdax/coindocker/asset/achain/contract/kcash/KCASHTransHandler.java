package com.ourdax.coindocker.asset.achain.contract.kcash;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.trans.DefaultTransHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@Component(value = "KCASHTransHandler")
@Lazy
public class KCASHTransHandler extends DefaultTransHandler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.KCASH;
  }

}
