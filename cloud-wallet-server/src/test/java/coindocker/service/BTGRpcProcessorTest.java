package coindocker.service;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.bitcoin.btg.BTGRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xj.x
 */
@Slf4j
public class BTGRpcProcessorTest extends AbstractTestCase {

    @Autowired
    private BTGRpcProcessor BTGRpcProcessor;

    @Test
    public void testGetLatestBlock() {
        Block latestBlock = BTGRpcProcessor.getLatestBlock();
        log.info("get BTG latest block, and the result is blockNumber={}，blockHash={}",
                latestBlock.getBlockNumber(), latestBlock.getBlockHash());
    }

    @Test
    public void testQueryBalance() {
        BigDecimal bigDecimal = BTGRpcProcessor.queryBalance();
        log.info("BTG wallet now has {} btg coins", bigDecimal);
    }

    @Test
    public void testQueryTrans() {
        Block block = new SimpleBlock(null, "0002565b9ed4f5dd73ec368c5a429d7e2277be8b5f7a905f2ba2b06448bf78f5");
        BlockTrans blockTrans = BTGRpcProcessor.queryTrans(block);
        log.info("query block chain transInfo by blockHash: {}, transInfo: \n {}", block.getBlockHash(), blockTrans);
    }

    @Test
    public void testQueryTransInfo() {
        // txId必须为钱包内的交易id，否则会报错
//        String txId = "6f8fd10be757b5683e443d201b909404b1e3b381ba4e1fda40d6965dbc78fb8c";
        String txId = "4ed1bbac3fd5ac7904183eb4a9e9e7522dcea747cfdbeb064aad83aae84326e1";
        List<TransInfo> transInfos = BTGRpcProcessor.queryTransInfo(txId);
        transInfos.forEach(info -> log.info("wallet transaction id: {} detail info: {}",txId, info));
    }

    @Test
    public void testTransfer() {
        // 转账操作前提：钱包余额>=转账金额
        RpcTransRequest request = new RpcTransRequest();
        request.setAmount(new BigDecimal("0.001"));
        request.setTo("mms1Snst8S5fkkh8a885hXY8NGyoysvSgd");
        BTGRpcProcessor.preTransfer(request);
        RpcTransResponse txIdResp = BTGRpcProcessor.defaultTransfer(request);
        log.info("transfer success and return trId = {}", txIdResp.getTxId());
    }

}