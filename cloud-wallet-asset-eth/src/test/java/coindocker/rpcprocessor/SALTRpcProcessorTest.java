package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.salt.SALTRpcProcessor;
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
public class SALTRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private SALTRpcProcessor saltRpcProcessor;


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = saltRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("4105676", null);
    BlockTrans blockTrans = saltRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = saltRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("4000010", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 4000010, saltRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = saltRpcProcessor.getLatestBlock();
    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = saltRpcProcessor.queryTrans(block);
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
    String txId = "0x34918aa30d27dcabe8108db5f48b28cb15eeb585c6910e2bb90a71390b77518f";
    System.out.println(saltRpcProcessor.queryTransInfo(txId));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("4000009", null);
    System.out.println(saltRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = saltRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryContractBalance() {
    BigDecimal contractBalance = saltRpcProcessor.queryBalance();
    System.out.println(contractBalance);
  }

  @Test
  public void testTransferOut() {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request.setAmount(new BigDecimal("1.00"));
    RpcTransResponse response = saltRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }
}
