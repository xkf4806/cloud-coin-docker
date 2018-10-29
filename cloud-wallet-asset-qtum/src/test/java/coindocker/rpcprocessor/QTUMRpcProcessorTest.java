package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.qtum.QTUMRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 23/1/2018
 */
public class QTUMRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private QTUMRpcProcessor qtumRpcProcessor;

  @Test
  public void testQueryLatest() {
    System.out.println(qtumRpcProcessor.getLatestBlock());
  }

  @Test
  public void testQueryTrans() {
    List<TransInfo> transInfos =
        qtumRpcProcessor.queryTransInfo("259a907fd126ee400365fcf010c9b6fba380e203a18d2442683348cf8e65ad8c");
    System.out.println(transInfos);
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock(null, "97aeddc8d6d62b50c3949e29e8dcd33b64229b086560fa11ced6f4c498720882");
    System.out.println(qtumRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    System.out.println(qtumRpcProcessor.queryBalance());
  }

  @Test
  public void testTransfer() {

    RpcTransRequest req = new RpcTransRequest();
    req.setAmount(new BigDecimal("0.4"));
    req.setTo("QjCqufqZsPLYiwztqjjx3trfjzwmmrwHk3");

    qtumRpcProcessor.preTransfer(req);
    RpcTransResponse response = qtumRpcProcessor.defaultTransfer(req);
    System.out.println(response);

  }




}
