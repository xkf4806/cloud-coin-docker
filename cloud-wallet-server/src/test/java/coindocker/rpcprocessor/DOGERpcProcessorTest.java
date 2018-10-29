package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.bitcoin.doge.DOGERpcProcessor;
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
public class DOGERpcProcessorTest extends AbstractTestCase {

  @Autowired
  private DOGERpcProcessor dogeRpcProcessor;

  @Test
  public void testQueryLatest() {
    System.out.println(dogeRpcProcessor.getLatestBlock());
  }

  @Test
  public void testQueryTrans() {
    List<TransInfo> transInfos =
        dogeRpcProcessor.queryTransInfo("cdfc915798ebe1dbbcb684c893c490224a229d2a2aed896af90ff51ce76904c4");
    System.out.println(transInfos);
  }

  @Test
  public void testQueryBlockTrans() {
     Block block = new SimpleBlock(null, "7bf0f41336112956f22f28323eca4e55fb715c36342086923492a8a325bf6c3f");
     dogeRpcProcessor.queryTrans(block);
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock(null, "6156386966065ea9ebe90c7bc8287f7b57fd62226aa395b5d35d0560f2534984");
    System.out.println(dogeRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    System.out.println(dogeRpcProcessor.queryBalance());
  }

  @Test
  public void testTransfer() {
    RpcTransRequest req = new RpcTransRequest();
    req.setAmount(new BigDecimal("10"));
    req.setTo("D6y5vAqc81ryJ2a3kJ5djBxUPYYq8TGHJe");
    dogeRpcProcessor.preTransfer(req);
    RpcTransResponse response = dogeRpcProcessor.defaultTransfer(req);
    System.out.println(response);
  }


}
