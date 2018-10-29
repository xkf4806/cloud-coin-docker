package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.Optional;

/**
 * @author think on 9/1/2018
 */
public interface CoinAddressService {
  Optional<String> getCoinAddress(AssetCode assetCode, String coinAddress);
}
