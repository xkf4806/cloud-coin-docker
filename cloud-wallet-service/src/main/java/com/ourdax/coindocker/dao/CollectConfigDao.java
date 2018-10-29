package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.CollectConfig;
import org.apache.ibatis.annotations.Param;

/**
 * Created by zhangjinyang on 2018/7/18.
 */
public interface CollectConfigDao {

  int insert(CollectConfig collectConfig);

  CollectConfig queryConfigsByAsset(@Param("assetCode") String assetCode);
}
