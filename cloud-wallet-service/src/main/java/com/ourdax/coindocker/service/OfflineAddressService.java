package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.CollectionRecords;
import java.math.BigDecimal;

/**
 * Created by zhangjinyang on 2018/8/3.
 */
public interface OfflineAddressService {

  String fetchUsefulOfflineAddress(AssetCode assetCode, BigDecimal amount);

  int updateAmountAndStatus(CollectionRecords record);
}
