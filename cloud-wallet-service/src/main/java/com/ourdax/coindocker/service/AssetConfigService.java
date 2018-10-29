package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;

/**
 * @author think on 11/1/2018
 */
public interface AssetConfigService {
  int getConfirmThreshold(AssetCode assetCode);
}
