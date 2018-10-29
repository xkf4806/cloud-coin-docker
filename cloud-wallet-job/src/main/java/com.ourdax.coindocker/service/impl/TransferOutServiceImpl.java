package com.ourdax.coindocker.service.impl;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.common.enums.ACTContractEnum;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.QRC20Enum;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.dao.TransferOutDao;
import com.ourdax.coindocker.domain.TransferOut;
import com.ourdax.coindocker.service.TransferOutService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author think on 10/1/2018
 */
@Service(value = "transferOutService")
public class TransferOutServiceImpl implements TransferOutService {

  private static final String TABLE_PREFIX = "transfer_out_";


  private static final int UNSENT_THRESHOLD_IN_DAYS = 10;

  private static final int PENDING_THRESHOLD_IN_MONTHS = 1;

  @Autowired
  private TransferOutDao transferOutDao;

  @Override
  public void save(AssetCode assetCode, TransferOut transferOut) {
    transferOutDao.insert(getTableName(assetCode), transferOut);
  }

  @Override
  public int updateWithdrawStatusById(AssetCode assetCode, TransferOut transferOut) {
    if (assetCode.isContract()) {
      assetCode = AssetCode.valueOf(transferOut.getAssetCode());
    }
    return transferOutDao.updateWithdrawStatusById(getTableName(assetCode), transferOut);
  }


  @Override
  public int updateTxId(AssetCode assetCode, TransferOut transferOut) {

    if (assetCode.isContract()) {
      assetCode = AssetCode.valueOf(transferOut.getAssetCode());
    }
    return transferOutDao.updateTxId(getTableName(assetCode), transferOut);
  }

  public TransferOut queryById(AssetCode assetCode, Integer id) {
    return transferOutDao.selectById(getTableName(assetCode), id);
  }

  @Override
  public List<TransferOut> queryPendings(AssetCode assetCode) {

    Date from = DateUtil.minusMonths(DateUtil.now(), PENDING_THRESHOLD_IN_MONTHS);

    ArrayList<TransferOut> resultList = Lists.newArrayList();
    switch (assetCode) {
      case QRC20:

        for (QRC20Enum coin : QRC20Enum.values()) {
          assetCode = AssetCode.valueOf(coin.name());
          List<TransferOut> transferOuts = transferOutDao
              .selectPendings(getTableName(assetCode), from);
          resultList.addAll(transferOuts);
        }
        return resultList;

      case ACTCONTRACT:

        for (ACTContractEnum coin : ACTContractEnum.values()) {
          assetCode = AssetCode.valueOf(coin.name());
          List<TransferOut> transferOuts = transferOutDao
              .selectPendings(getTableName(assetCode), from);
          resultList.addAll(transferOuts);
        }
        return resultList;

      default:
        return transferOutDao.selectPendings(getTableName(assetCode), from);
    }

  }

  @Override
  public List<TransferOut> queryUnsents(AssetCode assetCode) {

    Date from = DateUtil.minusDays(DateUtil.now(), UNSENT_THRESHOLD_IN_DAYS);
    ArrayList<TransferOut> resultList = Lists.newArrayList();
    switch (assetCode) {
      case QRC20:

        for (QRC20Enum coin : QRC20Enum.values()) {
          assetCode = AssetCode.valueOf(coin.name());
          List<TransferOut> transferOuts = transferOutDao
              .selectUnsents(getTableName(assetCode), from);
          resultList.addAll(transferOuts);
        }
        return resultList;
      case ACTCONTRACT:
        for (ACTContractEnum coin : ACTContractEnum.values()) {
          assetCode = AssetCode.valueOf(coin.name());
          List<TransferOut> transferOuts = transferOutDao
              .selectUnsents(getTableName(assetCode), from);
          resultList.addAll(transferOuts);
        }
        return resultList;
      default:
        return transferOutDao.selectUnsents(getTableName(assetCode), from);
    }

  }

  private String getTableName(AssetCode assetCode) {
    return TABLE_PREFIX + assetCode.name().toLowerCase();
  }
}
