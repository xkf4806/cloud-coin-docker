package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.asset.gnt.GNTRpcProcessor;
import com.ourdax.coindocker.asset.knc.KNCRpcProcessor;
import com.ourdax.coindocker.asset.link.LINKRpcProcessor;
import com.ourdax.coindocker.asset.lrc.LRCRpcProcessor;
import com.ourdax.coindocker.asset.mana.MANARpcProcessor;
import com.ourdax.coindocker.asset.nuls.NULSRpcProcessor;
import com.ourdax.coindocker.asset.pay.PAYRpcProcessor;
import com.ourdax.coindocker.asset.plc.PLCRpcProcessor;
import com.ourdax.coindocker.asset.rep.REPRpcProcessor;
import com.ourdax.coindocker.asset.zrx.ZRXRpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/1/16.
 */
public class BatchERC20RpcProcessorTest extends AbstractTestCase{

  @Autowired
  private LRCRpcProcessor lrcRpcProcessor;
  @Autowired
  private MANARpcProcessor manaRpcProcessor;
  @Autowired
  private NULSRpcProcessor nulsRpcProcessor;
  @Autowired
  private KNCRpcProcessor kncRpcProcessor;
  @Autowired
  private ZRXRpcProcessor zrxRpcProcessor;
  @Autowired
  private GNTRpcProcessor gntRpcProcessor;
  @Autowired
  private LINKRpcProcessor linkRpcProcessor;
  @Autowired
  private PAYRpcProcessor payRpcProcessor;
  @Autowired
  private PLCRpcProcessor plcRpcProcessor;
  @Autowired
  private REPRpcProcessor repRpcProcessor;

  @Test
  public void testQueryBalance() {
    BigDecimal balance = repRpcProcessor.queryBalance();
    System.out.println("ETH:" + balance);
  }

  @Test
  public void testQueryContractBalance() {

//    System.out.println(lrcRpcProcessor.queryContractBalance());
//
//    System.out.println(manaRpcProcessor.queryContractBalance());
//
//    System.out.println(nulsRpcProcessor.queryContractBalance());
//
//    System.out.println(kncRpcProcessor.queryContractBalance());
//
//    System.out.println(zrxRpcProcessor.queryContractBalance());
//
//    System.out.println(gntRpcProcessor.queryContractBalance());
//
//    System.out.println(linkRpcProcessor.queryContractBalance());
//
//    System.out.println(payRpcProcessor.queryContractBalance());
//
//    System.out.println(plcRpcProcessor.queryContractBalance());

    System.out.println(repRpcProcessor.queryBalance());

  }

  @Test
  public void testTransferOut()  {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0xbac9573be9c1f381c3789384b9afb851d300a4a5");
    request.setAmount(new BigDecimal("0.19"));
    repRpcProcessor.preTransfer(request);
    RpcTransResponse response = repRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

}
