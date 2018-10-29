package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.BlockCheck;

/**
 * @author think on 9/1/2018
 */
public interface BlockCheckService {
  BlockCheck queryLastBlock(AssetCode assetCode);

  void save(BlockCheck blockCheck);

  int updateBlock(BlockCheck blockCheck);
}
