package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.util.List;

/**
 * Created by zhangjinyang on 2018/7/18.
 */
public interface CollectionRecordsService {

  void checkIfNeedToCollectAndSave(AssetCode assetCode, TransferIn transferIn);

  List<CollectionRecords> queryUnHandledRecords(AssetCode assetCode);

  /**也需要记录手续费*/
  int updateStatus(CollectionRecords record);

  List<CollectionRecords> queryPendingRecords(AssetCode assetCode);

  List<CollectionRecords> queryUnChargedRecords(AssetCode assetCode);

  int updateRecordsStatusById(Integer collectionId, CollectOrderStatus status);
}
