package com.ourdax.coindocker.service.impl;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.dao.BlockCheckDao;
import com.ourdax.coindocker.domain.BlockCheck;
import com.ourdax.coindocker.service.BlockCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author think on 9/1/2018
 */
@Service
public class BlockCheckServiceImpl implements BlockCheckService {

  @Autowired
  private BlockCheckDao blockCheckDao;

  @Override
  public BlockCheck queryLastBlock(AssetCode assetCode) {
    return blockCheckDao.selectByAssetCode(assetCode.name());
  }

  @Override
  public void save(BlockCheck blockCheck) {
    blockCheckDao.insert(blockCheck);
  }

  @Override
  public int updateBlock(BlockCheck blockCheck) {
    return blockCheckDao.updateBlock(blockCheck);
  }
}
