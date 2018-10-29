package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Created by zhangjinyang on 2018/7/22.
 */
public interface CollectFeeChargeRecordDao {

  int insert(CollectFeeChargeRecord feeChargeRecord);

  List<CollectFeeChargeRecord> queryConfirmingRecords(@Param("assetCode") AssetCode assetCode);

  int update(CollectFeeChargeRecord record);
}
