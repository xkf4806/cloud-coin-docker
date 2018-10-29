package com.ourdax.coindocker.service.impl;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.common.enums.ACTContractEnum;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.QRC20Enum;
import com.ourdax.coindocker.common.enums.TypesOfAsset;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.dao.TransferInDao;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.domain.TransferIn.DepositStatus;
import com.ourdax.coindocker.service.CollectionRecordsService;
import com.ourdax.coindocker.service.TransferInService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author think on 10/1/2018
 */
@Service
@Slf4j
public class TransferInServiceImpl implements TransferInService {

  private static final String TABLE_PREFIX = "transfer_in_";

  @Autowired
  private TransferInDao transferInDao;

  @Autowired
  private CollectionRecordsService collectionRecordsService;

  @Override
  public void save(AssetCode assetCode, TransferIn transferIn) {
    transferInDao.insert(getTableName(assetCode), transferIn);
  }

  private String getTableName(AssetCode assetCode) {
    return TABLE_PREFIX + assetCode.name().toLowerCase();
  }

  @Override
  public List<TransferIn> queryPendings(AssetCode assetCode) {

    Date from = DateUtil.minusMonths(DateUtil.now(), 1);

    ArrayList<TransferIn> resultList = Lists.newArrayList();
    switch (assetCode) {
      case QRC20:

        for (QRC20Enum coin : QRC20Enum.values()) {
          assetCode = AssetCode.valueOf(coin.name());
          List<TransferIn> transferIns = transferInDao
              .selectPendings(getTableName(assetCode), from);
          resultList.addAll(transferIns);
        }
        return resultList;

      case ACTCONTRACT:

        for (ACTContractEnum coin : ACTContractEnum.values()) {
          assetCode = AssetCode.valueOf(coin.name());
          List<TransferIn> transferIns = transferInDao
              .selectPendings(getTableName(assetCode), from);
          resultList.addAll(transferIns);
        }
        return resultList;

      default:
        return transferInDao.selectPendings(getTableName(assetCode), from);
    }
  }

  @Override
  public int updateTransferStatus(AssetCode assetCode, TransferIn transferIn) {

    int num = transferInDao.updateTransferStatus(getTableName(assetCode), transferIn);
    /**归集*/
    if (TypesOfAsset.COLLECT_BY_ADDRESS_SERIES.contains(assetCode) && DepositStatus.CONFIRMED
        .equals(transferIn.getDepositStatus())) {
      collectionRecordsService.checkIfNeedToCollectAndSave(assetCode, transferIn);
    }
    return num;

  }

  @Override
  public boolean isTransExist(AssetCode assetCode, String txId, String to, String vout) {
    return transferInDao.selectCountByTxId(getTableName(assetCode), txId, to, vout) > 0;
  }

  @Override
  public Map<String, Integer> queryLastSeqOfSwtTxs(List<String> addresses) {

    Map<String, Integer> map = addresses.stream().map(transferInDao::selectLastestTxOfSwt)
        .filter(transferIns -> (!transferIns.isEmpty()))
        .map(transferIns -> transferIns.get(0))
        .collect(Collectors.toMap(TransferIn::getToCoinAddress, TransferIn::getConfirmNum));

    return map;
  }

  @Override
  public TransferIn querySWTTransByTxId(String txId) {
    return transferInDao.querySWTTransByTxId(txId);
  }
}
