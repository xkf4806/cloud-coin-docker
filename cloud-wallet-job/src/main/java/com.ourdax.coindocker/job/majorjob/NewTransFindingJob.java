package com.ourdax.coindocker.job.majorjob;

import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.domain.BlockCheck;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.service.BlockCheckService;
import com.ourdax.coindocker.trans.TransHandler;
import com.ourdax.coindocker.utils.TransactionHelper;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author think on 11/1/2018
 */
@Component
@Slf4j
public class NewTransFindingJob extends AbstractJob {

  @Autowired
  private AssetComponentManager manager;

  @Autowired
  private BlockCheckService blockCheckService;

  @Autowired
  private TransactionHelper transactionHelper;

  @Override
  public String getName() {
    return NewTransFindingJob.class.getSimpleName();
  }

  public void run(JobConfig config) {
    try {
      transactionHelper.doInCurrentTransaction(() -> doFind(config));
    } catch (Exception e) {
      log.error("NewTransFindingJob execute error", e);
    }
  }

  private void doFind(JobConfig config) {
      AssetCode assetCode = config.getAssetCode();
      log.info("NewTransFindingJob finding {} new transactions ...", config.getAssetCode());
      RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
      BlockCheck last = blockCheckService.queryLastBlock(assetCode);
      if (last == null) {
        saveBlockCheck(assetCode, rpcProcessor);
        return;
      }

      Block latestBlock = rpcProcessor.getLatestBlock();
      if (alreadyLatest(last, latestBlock)) {
        log.info("No more blocks to query for {}", assetCode);
        return;
      }

      Block block = new SimpleBlock(last.getBlockheight(), last.getBlockhash());
      BlockTrans blockTrans = rpcProcessor.queryTransSince(block);
      log.info("Processing {} transactions of block: ({}, {}], transactions.size: {}",
          assetCode, block.getBlockNumber(), blockTrans.getBlock().getBlockNumber(), blockTrans.getTrans().size());

      // handle new trans
      TransHandler transHandler = manager.getTransHandler(assetCode);
      blockTrans.getTrans().stream()
          .filter(transInfo -> transInfo.getAmount().compareTo(BigDecimal.ZERO) > 0)
          .forEach(transInfo -> transHandler.receiveNewTrans(assetCode, transInfo));

      // update block check process
      last.setBlockhash(blockTrans.getBlock().getBlockHash());
      last.setBlockheight(blockTrans.getBlock().getBlockNumber());
      last.setUpdateDate(DateUtil.now());
      blockCheckService.updateBlock(last);
  }


  private boolean alreadyLatest(BlockCheck last, Block latest) {
    return new BigInteger(last.getBlockheight()).compareTo(
        new BigInteger(latest.getBlockNumber()))  >= 0;
  }

  private void saveBlockCheck(AssetCode assetCode, RpcProcessor rpcProcessor) {
    Block latestBlock = rpcProcessor.getLatestBlock();
    BlockCheck blockCheck = new BlockCheck();
    blockCheck.setAssetCode(assetCode.name());
    blockCheck.setBlockhash(StringUtils.trimToEmpty(latestBlock.getBlockHash()));
    blockCheck.setBlockheight(String.valueOf(latestBlock.getBlockNumber()));
    blockCheckService.save(blockCheck);
  }
}
