package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.BlockCheck;
import org.apache.ibatis.annotations.Param;

/**
 * @author think on 9/1/2018
 */
public interface BlockCheckDao {

  BlockCheck selectByAssetCode(@Param("assetCode") String assetCode);

  void insert(BlockCheck blockCheck);

  int updateBlock(BlockCheck blockCheck);
}
