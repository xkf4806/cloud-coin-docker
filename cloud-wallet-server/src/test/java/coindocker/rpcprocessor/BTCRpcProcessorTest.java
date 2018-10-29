package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.asset.bitcoin.btc.BTCRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.rpc.RpcBatchTransferRequest;
import com.ourdax.coindocker.rpc.RpcBatchTransferResponse;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 23/1/2018
 */
public class BTCRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private BTCRpcProcessor btcRpcProcessor;

  @Test
  public void testQueryLatest() {
    System.out.println(btcRpcProcessor.getLatestBlock());
  }

  @Test
  public void testQueryTrans() {
    List<TransInfo> transInfos =
        btcRpcProcessor.queryTransInfo("586bc09f5958d91ccf55973a6727bdc1bd48a1cf362931a567e2808c8630fb87");
    System.out.println(transInfos);
  }

  @Test
  public void testQueryBlockTrans() {
     Block block = new SimpleBlock(null, "00000000000000000013869ef3953704a079134f8e059d29e0900ac72cee6869");
     btcRpcProcessor.queryTrans(block);
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock(null, "00000000000015fd030c923fbfb5ffd758b2998690dee11bf44b59b198c7ed39");
    System.out.println(btcRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    System.out.println(btcRpcProcessor.queryBalance());
  }

  @Test
  public void testTransfer() {
    RpcTransRequest req = new RpcTransRequest();
    req.setAmount(new BigDecimal("0.1"));
    req.setTo("n3CmNScVRD1k5XgZGCKWddJcVQAqWGhpF6");
    btcRpcProcessor.preTransfer(req);
    RpcTransResponse response = btcRpcProcessor.defaultTransfer(req);
    System.out.println(response);
  }


  @Test
  public void testBatchTransfer() {
    RpcBatchTransferRequest batchTransferRequest = new RpcBatchTransferRequest();

    RpcTransRequest req1 = new RpcTransRequest();
    req1.setAmount(new BigDecimal("0.01"));
    req1.setTo("moKhRUg1y15P8Y7ZLwU9Fet68EMLdiT5pN");

    RpcTransRequest req2 = new RpcTransRequest();
    req2.setAmount(new BigDecimal("0.01"));
    req2.setTo("muSghPxuWa3bkGdYCuVY27gDWfQvTY9ut6");


    List<RpcTransRequest> reqs = Lists.newArrayListWithExpectedSize(2);
    reqs.add(req1);
    reqs.add(req2);
    batchTransferRequest.setBatchRequests(reqs);
    RpcBatchTransferResponse response = btcRpcProcessor.batchTransfer(batchTransferRequest);
    System.out.println(response);
  }

}
