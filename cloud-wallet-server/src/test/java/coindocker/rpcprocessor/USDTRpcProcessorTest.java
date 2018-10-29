package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.usdt.USDTRpcProcessor;
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
public class USDTRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private USDTRpcProcessor usdtRpcProcessor;

  @Test
  public void testQueryLatest() {
    System.out.println(usdtRpcProcessor.getLatestBlock());
  }

  @Test
  public void testQueryTrans() {
    List<TransInfo> transInfos =
        usdtRpcProcessor.queryTransInfo("7914a9afa47fecce66f02415a9d72bcb4efa6d42d5ca471cea98c177a63ad726");
    System.out.println(transInfos);
  }

  @Test
  public void testQueryBlockTrans() {
     Block block = new SimpleBlock(null, "000000000000000000352d76af4c18d7ffc5f65ee4fb250c4c2c64579d7ed9d2");
     usdtRpcProcessor.queryTrans(block);
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock(null, "000000000000000000352d76af4c18d7ffc5f65ee4fb250c4c2c64579d7ed9d2");
    System.out.println(usdtRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    System.out.println(usdtRpcProcessor.queryBalance());
  }

  @Test
  public void testQueryBTCBalance() {
//    System.out.println(usdtRpcProcessor.queryBTCBalance());
  }

  @Test
  public void testTransfer() {

    RpcTransRequest req = new RpcTransRequest();
    req.setAmount(new BigDecimal("0.5"));
    req.setTo("14vnb2tyu9STVqjQeRNaBgPZJByQm9eBk5");
    RpcTransResponse response = usdtRpcProcessor.defaultTransfer(req);
    System.out.println(response);
  }


}
