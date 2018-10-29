package com.ourdax.coindocker.asset.etc;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.trans.DefaultTransHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/1/22.
 */
@Lazy
@Component(value = "ETCTransHandler")
public class ETCTransHandler extends DefaultTransHandler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ETC;
  }

}
