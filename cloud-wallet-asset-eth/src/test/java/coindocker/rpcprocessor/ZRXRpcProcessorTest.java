package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.zrx.ZRXRpcProcessor;
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
public class ZRXRpcProcessorTest extends AbstractTestCase{

  @Autowired
  private ZRXRpcProcessor zrxRpcProcessor;


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = zrxRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("4974244", null);
    BlockTrans blockTrans = zrxRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = zrxRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("4974244", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 4974244, zrxRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = zrxRpcProcessor.getLatestBlock();
    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = zrxRpcProcessor.queryTrans(block);
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
    String txId = "0x49b652ab8b126e59475b73fbfc093e0a6d5d6d1c4b96bc15abf3e90d3cb5e86b";
    System.out.println(zrxRpcProcessor.queryTransInfo(txId));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("4974243", null);
    System.out.println(zrxRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = zrxRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryContractBalance() {
    BigDecimal contractBalance = zrxRpcProcessor.queryBalance();
    System.out.println(contractBalance);
  }

  @Test
  public void testTransferOut() throws BizException {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request.setAmount(new BigDecimal("1.07"));
    RpcTransResponse response = zrxRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

}
