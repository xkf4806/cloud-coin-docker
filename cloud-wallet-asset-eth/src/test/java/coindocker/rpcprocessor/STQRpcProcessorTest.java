package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.stq.STQRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.exception.BizException;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/1/16.
 */
public class STQRpcProcessorTest extends AbstractTestCase{

  @Autowired
  private STQRpcProcessor stqRpcProcessor;


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = stqRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("4606526", null);
    BlockTrans blockTrans = stqRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = stqRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("4969150", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 4969150, stqRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = stqRpcProcessor.getLatestBlock();
//    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int start = 4614895;
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = stqRpcProcessor.queryTrans(block);
      if (blockTrans.getTrans().size() > 0) {
        System.out.println(blockTrans);
        count++;
        if (count > 10) {
          break;
        }
      }
      start--;
    }
  }

  @Test
  public void testQueryTransByTxId() {
    String txId = "0xed4530a7e2939163d173459c9005a9912c5498d2e10e8e3de9df957256caef22";
    System.out.println(stqRpcProcessor.queryTransInfo(txId));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("4969149", null);
    System.out.println(stqRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = stqRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryContractBalance() {
    BigDecimal contractBalance = stqRpcProcessor.queryBalance();
    System.out.println(contractBalance);
  }

  @Test
  public void testTransferOut() throws BizException {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request.setAmount(new BigDecimal("1.07"));
    RpcTransResponse response = stqRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

}
