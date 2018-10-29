package com.ourdax.coindocker.service.impl;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.dao.CollectFeeChargeRecordDao;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord;
import com.ourdax.coindocker.service.CollectFeeChargeRecordService;
import com.ourdax.coindocker.service.CollectionRecordsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhangjinyang on 2018/7/22.
 */
@Service
public class CollectFeeChargeRecordServiceImpl implements CollectFeeChargeRecordService {

  @Autowired
  private CollectFeeChargeRecordDao collectFeeChargeRecordDao;

  @Override
  public void saveFeeChargeRecord(CollectFeeChargeRecord feeChargeRecord) {
    collectFeeChargeRecordDao.insert(feeChargeRecord);
  }

  @Override
  public List<CollectFeeChargeRecord> queryPendingRecords(AssetCode assetCode) {
    return collectFeeChargeRecordDao.queryConfirmingRecords(assetCode);
  }

  @Override
  public void updateStatus(CollectFeeChargeRecord record) {
    collectFeeChargeRecordDao.update(record);
  }
}
