package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.asset.eth.ETHRpcProcessor;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.utils.EthUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthSyncing;

/**
 * Created by zhangjinyang on 2018/1/22.
 */
public class ETHRpcProcessorTest extends AbstractTestCase {

  @Autowired
  private ETHRpcProcessor ethRpcProcessor;

  @Autowired
  private Admin client;

  @Test
  public void testNonce() throws IOException {
    EthGetTransactionCount count = client
        .ethGetTransactionCount("0xbac9573be9c1f381c3789384b9afb851d300a4a5",
            DefaultBlockParameterName.LATEST).send();
    System.out.println(count.getTransactionCount());
  }

  @Test
  public void testGetAddressesBalance() throws IOException {
    BigInteger balance = client.ethGetBalance("0xDeb4F43AF694B855a944Ac0ABEED7BfA130BEE89",
        new DefaultBlockParameterNumber(
            BigInteger.valueOf(Long.parseLong(ethRpcProcessor.getLatestBlock().getBlockNumber()))))
        .send()
        .getBalance();
    System.out.println(EthUtils.fromWei(balance, 18));
  }


  @Test
  public void testQueryLatestBlock() {
    Block latestBlock = ethRpcProcessor.getLatestBlock();
    System.out.println(latestBlock);
  }

  @Test
  public void testSync() throws IOException {
    EthSyncing send = client.ethSyncing().send();
    System.out.println(JSON.toJSONString(send));
  }

  @Test
  public void testQueryTrans() {
    Block block = new SimpleBlock("5341631", null);
    BlockTrans blockTrans = ethRpcProcessor.queryTrans(block);
    System.out.println(blockTrans);
  }

  @Test
  public void testGetConfirmationNum() {
    Block latestBlock = ethRpcProcessor.getLatestBlock();
    Block block = new SimpleBlock("5341631", null);
    Assert.assertEquals(Integer.parseInt(latestBlock.getBlockNumber()) - 5341631,
        ethRpcProcessor.getConfirmationNum(block));

  }

  @Test
  public void testFindRecentTrans() {
    Block latestBlock = ethRpcProcessor.getLatestBlock();
    int start = Integer.parseInt(latestBlock.getBlockNumber());
    int count = 0;
    for (; ; ) {
      Block block = new SimpleBlock(String.valueOf(start), null);
      BlockTrans blockTrans = ethRpcProcessor.queryTrans(block);
      if (blockTrans.getTrans().size() > 0) {
        System.out.println(blockTrans);
        count++;
        if (count > 10) {
          break;
        }
      }
      start--;
    }
  }

  @Test
  public void testQueryTransByTxId() throws IOException {
    String txId = "0xa00d9250cb7d8ef1013298bd34df55338850ec1fb94e6361f1762aa749335a8d";
    List<TransInfo> transInfos = ethRpcProcessor.queryTransInfo(txId);
    System.out.println(ToStringBuilder.reflectionToString(transInfos));
  }

  @Test
  public void testQueryTransSince() {
    Block block = new SimpleBlock("5057431", null);
    System.out.println(ethRpcProcessor.queryTransSince(block));
  }

  @Test
  public void testQueryBalance() {
    BigDecimal balance = ethRpcProcessor.queryBalance();
    System.out.println(balance);
  }

  @Test
  public void testTransferOut() {
    RpcTransRequest request = new RpcTransRequest();
    request.setTo("0x1f5e35ca4652f6bcd57c2dfa75ef876da94fe090");
    request.setAmount(new BigDecimal("0.001"));
    ethRpcProcessor.preTransfer(request);
    RpcTransResponse response = ethRpcProcessor.defaultTransfer(request);
    System.out.println(response);
  }

  @Test
  public void testTransferOutBatch() {

    RpcTransRequest request1 = new RpcTransRequest();
    request1.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request1.setAmount(new BigDecimal("0.001"));

    RpcTransRequest request2 = new RpcTransRequest();
    request2.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request2.setAmount(new BigDecimal("0.002"));

    RpcTransRequest request3 = new RpcTransRequest();
    request3.setTo("0x8c1e6839af0b626d941b203def465f1f5224294f");
    request3.setAmount(new BigDecimal("0.001"));

    List<RpcTransRequest> requests = Lists.newArrayList(request1, request2, request3);
    requests.forEach(r -> {
      ethRpcProcessor.preTransfer(r);
      RpcTransResponse response = ethRpcProcessor.defaultTransfer(r);
      System.out.println(response);
    });

  }

  @Test
  public void testTransferByClient() throws IOException {

    String from = "0x8c1e6839af0b626d941b203def465f1f5224294f";
    PersonalUnlockAccount result = client.personalUnlockAccount(from, "123456").send();
    boolean unlockSuccess = result.getResult() != null && result.getResult();
    if (unlockSuccess) {
      EthSendTransaction transferOutResult = client.ethSendTransaction(Transaction
          .createEtherTransaction(from, null, BigInteger.valueOf(22_000_000_000L),
              BigInteger.valueOf(210000), "0x95476bd15f70fe5526d7050334e3fd4b3508ec94", EthUtils
                  .toWei(new BigDecimal("0.01"), 18)))
          .send();
      System.out.println(JSON.toJSONString(transferOutResult));
    }

  }

  @Test
  public void testGetNewAddress() throws IOException {

    /**
     * INSERT INTO `channel_coin_address_deposit_pool` (`uid`, `asset_code`, `name`, `coin_address`, `wallet_account`, `address_status`, `del_flag`, `create_date`, `update_date`)
     VALUES
     (0, 'ACT', '', 'ACTMvb3Qvv4Ffha7psAGjj6yL3qzMJs4iz668f772d8cdbc53a594c68ea30aec30d2', 'act', 'NEW', 'FALSE', now(), now());
     */
    FileWriter writer = new FileWriter("/Users/zhangjinyang/ETHAddresses.txt");
    FileWriter writer1 = new FileWriter("/Users/zhangjinyang/ETHAddresses.sql");
    BufferedWriter bufWriter = new BufferedWriter(writer);
    BufferedWriter bufWriter1 = new BufferedWriter(writer1);
    for (int i = 0; i < 3; i++) {
      NewAccountIdentifier send = client.personalNewAccount("123456").send();

      bufWriter.write(send.getAccountId());
      bufWriter.newLine();

      String sql =
          "INSERT INTO `channel_coin_address_deposit_pool` (`uid`, `asset_code`, `name`, `coin_address`, `wallet_account`, `address_status`, `del_flag`, `create_date`, `update_date`)\n"
              + "     VALUES\n"
              + "     (0, 'ETH', '', '" + send.getAccountId()
              + "', 'eth', 'NEW', 'FALSE', now(), now());";
      bufWriter1.write(sql);
      bufWriter1.newLine();
    }
    bufWriter.flush();
    bufWriter1.flush();
    bufWriter.close();
    bufWriter1.close();

  }


}
