package coindocker.assets;

import coindocker.ActAbstractTestCase;
import com.alibaba.fastjson.JSON;
import com.ourdax.coindocker.asset.achain.contract.ACTCONTRACTRpcProcessor;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/5/22.
 */
public class AchainContractTest extends ActAbstractTestCase{

  /**已经不是contract统一的procesor了 分开作为了单独的实例*/
  @Autowired
  private ACTCONTRACTRpcProcessor processor;

  @Test
  public void testQueryBalance(){
    BigDecimal balance = processor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testQueryTrans(){
    BlockTrans trans = processor
        .queryTrans(new SimpleBlock("2561807", "a6a8125916c20ddfb2f4bf4d773c21fd235a2c83"));
    System.out.println(JSON.toJSONString(trans));

  }

  @Test
  public void testQueryTx(){
    List<TransInfo> transInfos = processor.queryTransInfo("c9208ad93d04c87a31c464ff76d86caa75d77918");
    System.out.println(JSON.toJSONString(transInfos));
  }

  @Test
  public void testSend(){
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("ACTHTtuQUT285d8opyVXX6wAWa8ZS1ipu156");
    request.setAmount(new BigDecimal("0.1"));
    request.setAssetCode(AssetCode.CNT);
    RpcTransResponse response = processor.defaultTransfer(request);
    System.out.println(response);
  }
}
