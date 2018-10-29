package com.ourdax.coindocker.service.impl;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.dao.CollectionRecordsDao;
import com.ourdax.coindocker.dao.OfflineAddressDao;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.domain.OfflineAddress;
import com.ourdax.coindocker.domain.OfflineAddress.OfflineAddrStatus;
import com.ourdax.coindocker.service.OfflineAddressService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhangjinyang on 2018/8/3.
 */
@Slf4j
@Service
public class OfflineAddressServiceImpl implements OfflineAddressService {

  @Autowired
  private OfflineAddressDao offlineAddressDao;

  public String fetchUsefulOfflineAddress(AssetCode assetCode, BigDecimal amount) {

    if (assetCode.isERC20()) {
      assetCode = AssetCode.ERC20;
    }
    List<OfflineAddress> offlineAddressList = offlineAddressDao
        .queryAddressByStatus(assetCode.name(),
            Lists.newArrayList(OfflineAddrStatus.INUSE));
    OfflineAddress inUsedAddress = offlineAddressList.get(0);

    if (amount.add(inUsedAddress.getAmount()).compareTo(inUsedAddress.getMaxAmount()) < 0) {
      log.info("地址{}未达到限额，可用", inUsedAddress.getAddress());
      return inUsedAddress.getAddress();
    } else {
      List<OfflineAddress> offlineAddresses = offlineAddressDao
          .queryAddressByStatus(assetCode.name(), Lists.newArrayList(OfflineAddrStatus.UNASSIGN));
      OfflineAddress offlineAddressUnassigned = offlineAddresses.get(0);
      log.info("目标地址{}已达到限额，不可用， 可用的地址替换为{}", offlineAddressUnassigned.getAddress());
      return offlineAddressUnassigned.getAddress();
    }

  }

  @Override
  public int updateAmountAndStatus(CollectionRecords record) {

    OfflineAddress offlineAddress = offlineAddressDao
        .queryByAddressAndAsset(record.getTargetAddr(), record.getAssetCode());
    BigDecimal afterAmount = record.getAmount().add(offlineAddress.getAmount());
    if (OfflineAddrStatus.UNASSIGN.equals(offlineAddress.getStatus())) {
      offlineAddress.setStatus(OfflineAddrStatus.INUSE);
      offlineAddress.setAmount(afterAmount);
      OfflineAddress offlineAddressInUse = offlineAddressDao
          .queryAddressByStatus(record.getAssetCode(), Lists.newArrayList(OfflineAddrStatus.INUSE))
          .get(0);
      offlineAddressInUse.setStatus(OfflineAddrStatus.QUIT);
      return offlineAddressDao.updateBatch(Lists.newArrayList(offlineAddress, offlineAddressInUse));
    } else {
      offlineAddress.setAmount(afterAmount);
      return offlineAddressDao.update(offlineAddress);

    }

  }


}
