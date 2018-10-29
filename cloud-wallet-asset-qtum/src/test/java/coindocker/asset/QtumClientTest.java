package coindocker.asset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.common.Constants;
import com.neemre.btcdcli4j.core.common.DataFormats;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import com.ourdax.coindocker.common.clients.qtum.pojos.ContractTransaction;
import com.ourdax.coindocker.common.clients.qtum.pojos.TransactionReceipt;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/4/17.
 */
public class QtumClientTest {

  private QtumClient client;

  @Before
  public void getClient() throws BitcoindException, CommunicationException {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", "localhost");
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(3889));
    clientProperties.setProperty("node.bitcoind.rpc.user", "UNIQUE_RPC_USERNAME");
    clientProperties.setProperty("node.bitcoind.rpc.password", "UNIQUE_RPC_PASSWORD");
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    client = new QtumClient(null, clientProperties);
  }

  @Test
  public void testGetHexAddress() throws BitcoindException, CommunicationException {
    String hexAddress = client.getHexAddress("QU3S7fatQPMzWyct9xP4A11UCe1V2rJhfi ");
    System.out.println(hexAddress);
  }

  @Test
  public void testGetTransactions() throws BitcoindException, CommunicationException {
    Transaction transaction = client
        .getTransaction("669e6a5c443dd035e44eeaa62cc98439b7aa4e3c06aba6a1ddb41912b1afeaba", false);
    System.out.println(JSON.toJSONString(transaction));
  }

  @Test
  public void testListBlocks() throws BitcoindException, CommunicationException {
    SinceBlock block = client
        .listSinceBlock("ff0e887be01a0e3bf558a57a1aec119104b2a3aa319beb61d03dcef489437d78", 1,
            true);
    System.out.println(JSON.toJSONString(block));
  }

  @Test
  public void testJSONObject(){
    String exampleJson = "{\n"
        + "  \"address\": \"fe59cbc1704e89a698571413a81f0de9d8f00c69\",\n"
        + "  \"executionResult\": {\n"
        + "    \"gasUsed\": 23311,\n"
        + "    \"excepted\": \"None\",\n"
        + "    \"newAddress\": \"fe59cbc1704e89a698571413a81f0de9d8f00c69\",\n"
        + "    \"output\": \"0000000000000000000000000000000000000000000000000000000232aaf800\",\n"
        + "    \"codeDeposit\": 0,\n"
        + "    \"gasRefunded\": 0,\n"
        + "    \"depositSize\": 0,\n"
        + "    \"gasForDeposit\": 0\n"
        + "  },\n"
        + "  \"transactionReceipt\": {\n"
        + "    \"stateRoot\": \"bf124ebb406f3410a521a1de5d167e44513cb9efd251a1db7dc3c3724c6c4ee5\",\n"
        + "    \"gasUsed\": 23311,\n"
        + "    \"bloom\": \"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
        + "    \"log\": [\n"
        + "    ]\n"
        + "  }\n"
        + "}";

    JSONObject data = JSON.parseObject(exampleJson);
    JSONObject executionResult = JSONObject.parseObject(data.get("executionResult").toString());
    String balanceHex = executionResult.get("output").toString();
    System.out.println(new BigDecimal(Long.parseLong(balanceHex, 16)).divide(new BigDecimal(Math.pow(10,9))).stripTrailingZeros());

  }

  @Test
  public void testGetContractBalance() throws BitcoindException, CommunicationException {

    BigDecimal inkBalance = client
        .getContractBalance("fe59cbc1704e89a698571413a81f0de9d8f00c69",
            "QU3S7fatQPMzWyct9xP4A11UCe1V2rJhfi", 9);
    System.out.println(inkBalance.toString());

  }

  @Test
  public void testListTransactionsSinceBlock() throws BitcoindException, CommunicationException {

    List<ContractTransaction> searchLogs = client
        .getContractTransactionsSinceBlock(149143,149143,
            Lists.newArrayList("2e1b8528c07539b5dd9a76f3374adf09f1ab6075"));
    System.out.println(JSON.toJSONString(searchLogs));

  }

  @Test
  public void testGetTransactionReceipt() throws BitcoindException, CommunicationException {
    TransactionReceipt receipt = client
        .getTransactionReceipt("b665d916760dad6cde95ec0a356f3a7a17e07fb61450cad9e2a9ac354cfdd6a2");
    System.out.println(JSON.toJSONString(receipt));
  }

  @Test
  public void testSendToContract() throws URISyntaxException, IOException {
//    client.sendContractTx("2e1b8528c07539b5dd9a76f3374adf09f1ab6075", "QU3S7fatQPMzWyct9xP4A11UCe1V2rJhfi", new BigDecimal(10), "250000", "0.00000040", "QLsRD76FWFBWy9uYgmc6wB6hcuzhdVYYbZ", 18);
//    Properties clientProperties = new Properties();
//    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
//    clientProperties.setProperty("node.bitcoind.rpc.host", "localhost");
//    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(3889));
//    clientProperties.setProperty("node.bitcoind.rpc.user", "UNIQUE_RPC_USERNAME");
//    clientProperties.setProperty("node.bitcoind.rpc.password", "UNIQUE_RPC_PASSWORD");
//    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");

    String requestJson = "{\n"
        + "    \"method\":\"sendtocontract\",\n"
        + "    \"params\":[\n"
        + "        \"2e1b8528c07539b5dd9a76f3374adf09f1ab6075\",\n"
        + "        \"a9059cbb00000000000000000000000002eb3123bc6ff8f5c401a078aaf5202c79730de70000000000000000000000000000000000000000000000008ac7230489e80000\",\n"
        + "        0,\n"
        + "        250000,\n"
        + "        0.0000004,\n"
        + "        \"QU3S7fatQPMzWyct9xP4A11UCe1V2rJhfi\"\n"
        + "    ],\n"
        + "    \"jsonrpc\":\"1.0\",\n"
        + "    \"id\":\"9120c267d19548556754048c4f13ab43\"\n"
        + "}";

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost();
    httpPost.setEntity(new StringEntity(requestJson, ContentType
        .create(DataFormats.JSON.getMediaType(), Constants.UTF_8)));
    String auth = Base64.encodeBase64String(("UNIQUE_RPC_USERNAME:UNIQUE_RPC_PASSWORD").getBytes());
    httpPost.addHeader(new BasicHeader("Authorization", "Basic " + auth));
    httpPost.setURI(new URI("http://localhost:3889"));
    CloseableHttpResponse response = httpClient.execute(httpPost);
    String responseJson = EntityUtils.toString(response.getEntity());
    System.out.println(responseJson);
  }

  @Test
  public void testGetBlock() throws BitcoindException, CommunicationException {
    Block block = client
        .getBlock("2b084b6732d72adfcea9928d56a7e2f618f10e79ee9d5cd78024e9917fbbba71");
    System.out.println(block.getHeight() + ", " + block.getHash());
  }


}
