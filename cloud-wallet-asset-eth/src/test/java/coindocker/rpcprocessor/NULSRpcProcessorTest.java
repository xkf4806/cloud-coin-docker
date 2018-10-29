package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.nuls.NULSRpcProcessor;
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
public class NULSRpcProcessorTest extends AbstractTestCase{

  @Autowired
  private NULSRpcProcessor nulsRpcProcessor;


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = nulsRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("4973272", null);
    BlockTrans blockTrans = nulsRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = nulsRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("4917164", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 4000010, nulsRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = nulsRpcProcessor.getLatestBlock();
    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = nulsRpcProcessor.queryTrans(block);
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
    String txId = "0xb5427976e85070c1660b8b5dabea56c1389f08e81937d2ba9887c06b8e14b905";
    System.out.println(nulsRpcProcessor.queryTransInfo(txId));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("4973271", null);
    System.out.println(nulsRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = nulsRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryContractBalance() {
    BigDecimal contractBalance = nulsRpcProcessor.queryBalance();
    System.out.println(contractBalance);
  }

  @Test
  public void testTransferOut() throws BizException {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request.setAmount(new BigDecimal("1.07"));
    RpcTransResponse response = nulsRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

}
