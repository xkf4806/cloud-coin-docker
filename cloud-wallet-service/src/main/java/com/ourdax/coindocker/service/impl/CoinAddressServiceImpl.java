package com.ourdax.coindocker.service.impl;

import com.google.common.base.Strings;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.dao.AddressPoolDao;
import com.ourdax.coindocker.dao.ChannelCoinAddressDepositDao;
import com.ourdax.coindocker.domain.AddressPool;
import com.ourdax.coindocker.service.CoinAddressService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author think on 9/1/2018
 */
@Service
public class CoinAddressServiceImpl implements CoinAddressService {

  @Autowired
  private AddressPoolDao addressPoolDao;

  private static final String TABLE_PREFIX = "address_pool_";

  @Override
  public Optional<String> getCoinAddress(AssetCode assetCode, String coinAddress) {
    AddressPool addressPool = addressPoolDao
        .selectCoinAddress(TABLE_PREFIX + assetCode.name().toLowerCase(), coinAddress);
    if (addressPool != null){
      String address = addressPool.getCoinAddress();
      return Optional.ofNullable(Strings.emptyToNull(address));
    }
    return Optional.empty();

  }
}
