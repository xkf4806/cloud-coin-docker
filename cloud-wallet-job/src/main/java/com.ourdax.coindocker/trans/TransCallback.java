package com.ourdax.coindocker.trans;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.block.TransInfo;

/**
 * @author think on 14/1/2018
 */
public interface TransCallback<T> {
  void onConfirmed(AssetCode assetCode, TransInfo transInfo, T data);
  void onFailed(AssetCode assetCode, TransInfo transInfo, T data);
  void onConfirming(AssetCode assetCode, TransInfo transInfo, T data);
}
