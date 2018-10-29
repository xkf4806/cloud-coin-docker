package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.TransferOut;
import java.util.List;

/**
 * @author think on 10/1/2018
 */
public interface TransferOutService {
  void save(AssetCode assetCode, TransferOut transferOut);

  int updateWithdrawStatusById(AssetCode assetCode, TransferOut transferOut);

  int updateTxId(AssetCode assetCode, TransferOut transferOut);

  List<TransferOut> queryPendings(AssetCode assetCode);

  List<TransferOut> queryUnsents(AssetCode assetCode);

  TransferOut queryById(AssetCode assetCode, Integer id);
}
