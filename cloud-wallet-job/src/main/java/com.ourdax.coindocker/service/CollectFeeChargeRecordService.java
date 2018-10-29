package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord;
import java.util.List;

/**
 * Created by zhangjinyang on 2018/7/22.
 */
public interface CollectFeeChargeRecordService {

  void saveFeeChargeRecord(CollectFeeChargeRecord feeChargeRecord);

  List<CollectFeeChargeRecord> queryPendingRecords(AssetCode assetCode);

  void updateStatus(CollectFeeChargeRecord record);
}
