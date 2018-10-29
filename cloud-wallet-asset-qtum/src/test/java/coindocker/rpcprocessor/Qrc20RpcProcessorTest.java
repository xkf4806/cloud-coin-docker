package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.alibaba.fastjson.JSON;
import com.ourdax.coindocker.asset.qtum.qrc20.QRC20RpcProcessor;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/5/5.
 */
public class Qrc20RpcProcessorTest extends AbstractTestCase{

  private QRC20RpcProcessor qrc20RpcProcessor;



  @Test
  public void testQueryTxnById(){
    /**todo 无法获取到转入的交易信息*/
    List<TransInfo> transInfos = qrc20RpcProcessor.queryTransInfo("9922417d1e277e07e7b3e9ac82348f943ac70dc4e81a752e6f8e5c6f75bb6de7");
    System.out.println(JSON.toJSONString(transInfos));
  }

  @Test
  public void testQueryContractBalance(){
    BigDecimal balance = qrc20RpcProcessor.queryBalance();
    System.out.println(balance);

  }

  @Test
  public void testQueryTxnsSince(){
    BlockTrans blockTrans = qrc20RpcProcessor.queryTransSince(new SimpleBlock("149142",
        "6f0558e5e72f32fcbf395d45a35e9f3da117e5c7452a66c01c4cce56917d6ba8"));
    System.out.println(JSON.toJSONString(blockTrans));
  }

  @Test
  public void testQueryBalance(){
    System.out.println(qrc20RpcProcessor.queryBalance());
  }

  @Test
  public void testSend(){
    RpcTransRequest request = new RpcTransRequest();
    request.setFrom("QU3S7fatQPMzWyct9xP4A11UCe1V2rJhfi");
    request.setTo("QLsRD76FWFBWy9uYgmc6wB6hcuzhdVYYbZ");
    request.setAmount(new BigDecimal(10));
    request.setAssetCode(AssetCode.EPC);
    qrc20RpcProcessor.preTransfer(request);
    RpcTransResponse transfer = qrc20RpcProcessor.defaultTransfer(request);
    System.out.println(transfer.getTxId());

  }
}
