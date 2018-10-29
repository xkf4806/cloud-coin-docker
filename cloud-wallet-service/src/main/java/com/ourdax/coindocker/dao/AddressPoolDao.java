package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.AddressPool;
import org.apache.ibatis.annotations.Param;

/**
 * Address pool divided by assets
 */
public interface AddressPoolDao {

  AddressPool selectCoinAddress(@Param("table") String tableName,
      @Param("coinAddress") String coinAddress);

  AddressPool selectOneUnusedAddress(@Param("table") String tableName);

  int updateById(@Param("table") String tableName, @Param("addressPool") AddressPool addressPool);

}
