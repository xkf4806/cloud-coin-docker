package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.ChannelCoinAddressDeposit;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author think on 9/1/2018
 */
public interface ChannelCoinAddressDepositDao {

  String selectCoinAddress(@Param("assetCode") String assetCode,
      @Param("coinAddress") String coinAddress);

  List<ChannelCoinAddressDeposit> selectAssignedSwtAddress();

  int updateSWTAddressToAssigned(String address);
}
