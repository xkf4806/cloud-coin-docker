package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.asset.etc.ETCRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.admin.Admin;

/**
 * Created by zhangjinyang on 2018/1/22.
 */
public class ETCRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private ETCRpcProcessor etcRpcProcessor;

  @Autowired
  private Admin client;

//  @Before
//  public void init(){
//    client = Admin.build(new HttpService("http://localhost:8546"));
//  }

  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = etcRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("5341631", null);
    BlockTrans blockTrans = etcRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = etcRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("5341631", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 5341631, etcRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = etcRpcProcessor.getLatestBlock();
    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int count = 0;
    for (;;) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = etcRpcProcessor.queryTrans(block);
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
  public void testQueryTransByTxId() throws IOException {
    String txId = "0x9a30b5826a0c6ed97b71d0c578d42a0fd9a3116d3e01b7ea944306c846513b8f";
    List<TransInfo> transInfos = etcRpcProcessor.queryTransInfo(txId);
    System.out.println(ToStringBuilder.reflectionToString(transInfos));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("5457987", null);
    System.out.println(etcRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = etcRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testTransferOut() {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x6bea132b1ac7b83ab49809a1483a5a744ed22721");
    request.setAmount(new BigDecimal("0.1"));
    etcRpcProcessor.preTransfer(request);
    RpcTransResponse response = etcRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

  @Test
  public void testTransferOutBatch(){

    RpcTransRequest request1 = new RpcTransRequest();
    request1.setTo("0x6bea132b1ac7b83ab49809a1483a5a744ed22721");
    request1.setAmount(new BigDecimal("0.001"));

    RpcTransRequest request2 = new RpcTransRequest();
    request2.setTo("0x6bea132b1ac7b83ab49809a1483a5a744ed22721");
    request2.setAmount(new BigDecimal("0.002"));

    RpcTransRequest request3 = new RpcTransRequest();
    request3.setTo("0x6bea132b1ac7b83ab49809a1483a5a744ed22721");
    request3.setAmount(new BigDecimal("0.001"));

    List<RpcTransRequest> requests = Lists.newArrayList(request1, request2, request3);
    requests.forEach(r -> {
      etcRpcProcessor.preTransfer(r);
      RpcTransResponse response = etcRpcProcessor.defaultTransfer(r);
      System.out.println(response);
    });
  }

}
