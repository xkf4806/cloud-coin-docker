package coindocker.assets;

import com.alibaba.fastjson.JSON;
import coindocker.ActAbstractTestCase;
import com.ourdax.coindocker.common.clients.achain.AchainClient;
import com.ourdax.coindocker.common.clients.achain.AchainClientException;
import com.ourdax.coindocker.common.clients.achain.pojo.AchainBlock;
import com.ourdax.coindocker.common.clients.achain.pojo.WalletInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/5/22.
 */
public class AchainClientTest extends ActAbstractTestCase{

  @Autowired
  private AchainClient client;

  @Test
  public void testAchainBlock() throws AchainClientException {
    Long blockCount = client.getBlockCount();
    System.out.println(blockCount);
  }

  @Test
  public void testAccount() throws AchainClientException {
    WalletInfo info = client.getWalletInfo("wallet0511");
    System.out.println(JSON.toJSONString(info));
  }

  @Test
  public void testLastBlock() throws AchainClientException {
    AchainBlock blockInfo = client.getBlockInfo(2959024l);
    System.out.println(JSON.toJSONString(blockInfo));

  }

  @Test
  public void testBalance() throws AchainClientException {
    Long balance = client.getBalance("cloudtest1");
    System.out.println(balance);
  }

  @Test
  public void testGetMajorAddressByAccount(){

  }

//  public void testCreateWallet(){
//  }



}
