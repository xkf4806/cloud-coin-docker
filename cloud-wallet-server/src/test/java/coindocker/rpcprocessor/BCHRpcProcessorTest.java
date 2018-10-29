package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.asset.bitcoin.bch.BCHRpcProcessor;
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
public class BCHRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private BCHRpcProcessor bchRpcProcessor;

  @Test
  public void testQueryLatest() {
    System.out.println(bchRpcProcessor.getLatestBlock());
  }

  @Test
  public void testQueryTrans() {
    List<TransInfo> transInfos =
        bchRpcProcessor.queryTransInfo("18e9cff4a3889ee5bc9784cb76fc633c50748648990f329e2cbae337fee28946");
    System.out.println(transInfos);
  }

  @Test
  public void testQueryBlockTrans() {
     Block block = new SimpleBlock(null, "0000000000000000026a36cd7ae3c4faaea20b656264708c5d4e9eccb426bcb2");
     bchRpcProcessor.queryTrans(block);
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock(null, "0000000000000000026a36cd7ae3c4faaea20b656264708c5d4e9eccb426bcb2");
    System.out.println(bchRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    System.out.println(bchRpcProcessor.queryBalance());
  }

  @Test
  public void testTransfer() {
    RpcTransRequest req = new RpcTransRequest();
    req.setAmount(new BigDecimal("0.0001"));
    req.setTo("1EJv2qzoJxZfUQT6ehv3XAHtxKNqz8PsfL");
    RpcTransResponse response = bchRpcProcessor.defaultTransfer(req);
    System.out.println(response);
  }


  @Test
  public void testBatchTransfer() {
    RpcBatchTransferRequest batchTransferRequest = new RpcBatchTransferRequest();


    RpcTransRequest req1 = new RpcTransRequest();
    req1.setAmount(new BigDecimal("0.001"));
    req1.setTo("1EJv2qzoJxZfUQT6ehv3XAHtxKNqz8PsfL");

    RpcTransRequest req2 = new RpcTransRequest();
    req2.setAmount(new BigDecimal("0.001"));
    req2.setTo("1EJv2qzoJxZfUQT6ehv3XAHtxKNqz8PsfL");


    List<RpcTransRequest> reqs = Lists.newArrayListWithExpectedSize(2);
    reqs.add(req1);
    reqs.add(req2);
    batchTransferRequest.setBatchRequests(reqs);
    RpcBatchTransferResponse response = bchRpcProcessor.batchTransfer(batchTransferRequest);
    System.out.println(response);
  }

}
