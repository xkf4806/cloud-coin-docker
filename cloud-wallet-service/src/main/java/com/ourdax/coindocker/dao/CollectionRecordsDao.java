package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.domain.CollectionRecords;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Created by zhangjinyang on 2018/7/18.
 */
public interface CollectionRecordsDao {

  int insert(CollectionRecords records);

  List<CollectionRecords> queryRecordsByAssetAndStatus(@Param("assetCode") AssetCode assetCode,
      @Param("collectOrderStatuses") List<CollectOrderStatus> collectOrderStatuses);

  int update(CollectionRecords record);

  int updateStatusById(@Param("collectionId") Integer collectionId,
      @Param("status") CollectOrderStatus status);

  BigDecimal queryTotalUnCollectedAmountByTargetAddress(@Param("assetCode") AssetCode assetCode,
      @Param("collectOrderStatuses") ArrayList<CollectOrderStatus> collectOrderStatuses);
}
