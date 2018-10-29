package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.wtc.WTCRpcProcessor;
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
 * @author think on 15/1/2018
 */
public class WTCRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private WTCRpcProcessor wtcRpcProcessor;


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = wtcRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("4000000", null);
    BlockTrans blockTrans = wtcRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = wtcRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("4000010", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 4000010, wtcRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = wtcRpcProcessor.getLatestBlock();
    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = wtcRpcProcessor.queryTrans(block);
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
    String txId = "0x4bc61ac972e1411ae093a0da9fd0fc855ada62375d7a3adff7ba17b0d0d5d393";
    System.out.println(wtcRpcProcessor.queryTransInfo(txId));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("4000009", null);
    System.out.println(wtcRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = wtcRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryContractBalance() {
    BigDecimal contractBalance = wtcRpcProcessor.queryBalance();
    System.out.println(contractBalance);
  }

  @Test
  public void testTransferOut() {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request.setAmount(new BigDecimal("1.03"));
    RpcTransResponse response = wtcRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }
}
