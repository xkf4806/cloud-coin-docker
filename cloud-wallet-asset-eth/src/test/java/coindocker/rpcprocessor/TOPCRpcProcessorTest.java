package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.topc.TOPCRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/1/16.
 */
public class TOPCRpcProcessorTest extends AbstractTestCase{

  @Autowired
  private TOPCRpcProcessor topcRpcProcessor;


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = topcRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("4933163", null);
    BlockTrans blockTrans = topcRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = topcRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("4933163", null);
//    System.out.println(topcRpcProcessor.getConfirmationNum(block));
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 4933163, topcRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
//    Block latestBlock = topcRpcProcessor.getLatestBlock();
//    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int start = 4933165;
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = topcRpcProcessor.queryTrans(block);
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
    String txId = "0xc89622e6e9f7ff26feb3ff172b2653b20f39386e3eb391f9bda48979ba34c19e";
    System.out.println(topcRpcProcessor.queryTransInfo(txId));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("4933100", null);
    System.out.println(topcRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = topcRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryContractBalance() {
    BigDecimal contractBalance = topcRpcProcessor.queryBalance();
    System.out.println(contractBalance);
  }

  @Test
  public void testTransferOut() {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request.setAmount(new BigDecimal("2.1"));
    RpcTransResponse response = topcRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

}
