package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.asset.bitcoin.ltc.LTCRpcProcessor;
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
public class LTCRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private LTCRpcProcessor ltcRpcProcessor;

  @Test
  public void testQueryLatest() {
    System.out.println(ltcRpcProcessor.getLatestBlock());
  }

  @Test
  public void testQueryTrans() {
    List<TransInfo> transInfos =
        ltcRpcProcessor.queryTransInfo("a69a21d3bb20bd5be6d8febd891b86284e753b99905d6a5cdd5f77987b489a3e");
    System.out.println(transInfos);
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock(null, "1cfa7e589000ebfdf7d52930902ebdf18ba414c091b05e784decd00ebaa57762");
    System.out.println(ltcRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    System.out.println(ltcRpcProcessor.queryBalance());
  }

  @Test
  public void testTransfer() {
    RpcTransRequest req = new RpcTransRequest();
    req.setAmount(new BigDecimal("0.1"));
    req.setTo("mvDGZdo2MuLkpAET8BjVMDcpe3HLPwdkrL");
    RpcTransResponse response = ltcRpcProcessor.defaultTransfer(req);
    System.out.println(response);
  }


  @Test
  public void testBatchTransfer() {
    RpcBatchTransferRequest batchTransferRequest = new RpcBatchTransferRequest();

    RpcTransRequest req1 = new RpcTransRequest();
    req1.setAmount(new BigDecimal("0.01"));
    req1.setTo("mp1zCczaPT3D5knZ4MbjtDhDtxusxxVPcz");

    RpcTransRequest req2 = new RpcTransRequest();
    req2.setAmount(new BigDecimal("0.01"));
    req2.setTo("mvDGZdo2MuLkpAET8BjVMDcpe3HLPwdkrL");


    List<RpcTransRequest> reqs = Lists.newArrayListWithExpectedSize(2);
    reqs.add(req1);
    reqs.add(req2);
    batchTransferRequest.setBatchRequests(reqs);
    RpcBatchTransferResponse response = ltcRpcProcessor.batchTransfer(batchTransferRequest);
    System.out.println(response);
  }

}
