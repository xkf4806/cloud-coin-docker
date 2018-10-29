package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.OfflineAddress;
import com.ourdax.coindocker.domain.OfflineAddress.OfflineAddrStatus;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Created by zhangjinyang on 2018/7/19.
 */
public interface OfflineAddressDao {

  List<OfflineAddress> queryAddressByStatus(@Param("assetCode") String assetCode,
      @Param("offlineAddrStatuses") List<OfflineAddrStatus> offlineAddrStatuses);

  int update(OfflineAddress inUsedAddress);

  int updateBatch(@Param("offlineAddresses") ArrayList<OfflineAddress> offlineAddresses);

  OfflineAddress queryByAddressAndAsset(@Param("targetAddress") String targetAddress, @Param("asset") String asset);
}
