package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.TransferIn;
import java.util.List;
import java.util.Map;

/**
 * @author think on 10/1/2018
 */
public interface TransferInService {
  void save(AssetCode assetCode, TransferIn transferIn);

  List<TransferIn> queryPendings(AssetCode assetCode);

  int updateTransferStatus(AssetCode assetCode, TransferIn transferIn);


  boolean isTransExist(AssetCode assetCode, String txId, String to, String vout);

  Map<String,Integer> queryLastSeqOfSwtTxs(List<String> addresses);

  TransferIn querySWTTransByTxId(String txId);
}
