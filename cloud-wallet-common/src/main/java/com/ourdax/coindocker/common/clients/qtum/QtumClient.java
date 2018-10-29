package com.ourdax.coindocker.common.clients.qtum;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.Commands;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.ClientConfigurator;
import com.neemre.btcdcli4j.core.common.Constants;
import com.neemre.btcdcli4j.core.common.DataFormats;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Output;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClient;
import com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClientImpl;
import com.neemre.btcdcli4j.core.util.CollectionUtils;
import com.ourdax.coindocker.common.clients.qtum.pojos.ContractTransaction;
import com.ourdax.coindocker.common.clients.qtum.pojos.QtumInfo;
import com.ourdax.coindocker.common.clients.qtum.pojos.TransactionReceipt;
import com.ourdax.coindocker.common.clients.qtum.pojos.TxnSendResponse;
import com.ourdax.coindocker.common.utils.IDGeneratorUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangjinyang on 2018/3/7.
 */
public class QtumClient {

  private static final Logger LOG = LoggerFactory.getLogger(QtumClient.class);

  private static final String GET_CONTRACT_BALANCE_CODE = "70a08231";

  private static final String SEND_CONTRACT_TX_CODE = "a9059cbb";

  private ClientConfigurator configurator;

  private JsonRpcClient rpcClient;

  private Properties nodeConfig;

  public QtumClient(CloseableHttpClient httpProvider, Properties nodeConfig)
      throws BitcoindException, CommunicationException {
    LOG.info(">> initialize(..): initiating the 'omnicoind' core wrapper");
    configurator = new ClientConfigurator();
    rpcClient = new JsonRpcClientImpl(configurator.checkHttpProvider(httpProvider),
        configurator.checkNodeConfig(nodeConfig));
    configurator.checkNodeVersion(getInfo().getVersion());
    configurator.checkNodeHealth((Block) getBlock(getBestBlockHash(), true));
    this.nodeConfig = nodeConfig;

  }


  public Transaction getTransaction(String txId, boolean withWatchOnly) throws BitcoindException,
      CommunicationException {
    List params = CollectionUtils.asList(new Object[]{txId, withWatchOnly});
    String transactionJson = this.rpcClient.execute(Commands.GET_TRANSACTION.getName(), params);
    Transaction transaction = this.rpcClient.getMapper()
        .mapToEntity(transactionJson, Transaction.class);
    return transaction;
  }

  public TransactionReceipt getTransactionReceipt(String txId)
      throws BitcoindException, CommunicationException {

    List params = CollectionUtils.asList(new Object[]{txId});
    String responseJson = this.rpcClient.execute(QtumCommands.GET_TRANSACTION_RECEIPT, params);
    JSONArray jsonArray = JSON.parseArray(responseJson);
    if (!jsonArray.isEmpty()) {
      TransactionReceipt transactionReceipt = JSON
          .parseObject(jsonArray.get(0).toString(), TransactionReceipt.class);
      return transactionReceipt;
    }
    return null;

  }

  public BigDecimal getBalance() throws BitcoindException,
      CommunicationException {
    String balanceJson = this.rpcClient.execute(Commands.GET_BALANCE.getName());
    BigDecimal balance = this.rpcClient.getParser().parseBigDecimal(balanceJson);
    return balance.stripTrailingZeros();
  }

  public QtumInfo getInfo() throws BitcoindException, CommunicationException {
    String infoJson = rpcClient.execute(Commands.GET_INFO.getName());
    QtumInfo info = rpcClient.getMapper().mapToEntity(infoJson, QtumInfo.class);
    return info;
  }

  public Block getBlock(String headerHash) throws BitcoindException, CommunicationException {
    String blockJson = rpcClient.execute(Commands.GET_BLOCK.getName(), headerHash);
    Block block = rpcClient.getMapper().mapToEntity(blockJson, Block.class);
    return block;
  }

  public Object getBlock(String headerHash, Boolean isDecoded) throws BitcoindException,
      CommunicationException {
    List<Object> params = CollectionUtils.asList(headerHash, isDecoded);
    String blockJson = rpcClient.execute(Commands.GET_BLOCK.getName(), params);
    if (isDecoded) {
      Block block = rpcClient.getMapper().mapToEntity(blockJson, Block.class);
      return block;
    } else {
      String block = rpcClient.getParser().parseString(blockJson);
      return block;
    }
  }

  public String getBestBlockHash() throws BitcoindException, CommunicationException {
    String headerHashJson = rpcClient.execute(Commands.GET_BEST_BLOCK_HASH.getName());
    String headerHash = rpcClient.getParser().parseString(headerHashJson);
    return headerHash;
  }

  public SinceBlock listSinceBlock(String headerHash, Integer confirmations,
      Boolean withWatchOnly) throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(headerHash, confirmations, withWatchOnly);
    String sinceBlockJson = rpcClient.execute(Commands.LIST_SINCE_BLOCK.getName(), params);
    SinceBlock sinceBlock = rpcClient.getMapper().mapToEntity(sinceBlockJson, SinceBlock.class);
    return sinceBlock;
  }

  public String getBlockHash(Integer blockHeight) throws BitcoindException,
      CommunicationException {
    String headerHashJson = rpcClient.execute(Commands.GET_BLOCK_HASH.getName(), blockHeight);
    String headerHash = rpcClient.getParser().parseString(headerHashJson);
    return headerHash;
  }

  public String sendToAddress(String to, BigDecimal amount)
      throws BitcoindException, CommunicationException {

    List params = CollectionUtils.asList(new Object[]{to, amount});
    String transactionIdJson = this.rpcClient.execute(Commands.SEND_TO_ADDRESS.getName(), params);
    String transactionId = this.rpcClient.getParser().parseString(transactionIdJson);
    return transactionId;

  }

  public String getHexAddress(String address) throws BitcoindException, CommunicationException {

    String hexAddressJson = this.rpcClient.execute(QtumCommands.GET_HEX_ADDRESS, address);
    String hexAddress = this.rpcClient.getParser().parseString(hexAddressJson);
    return hexAddress;
  }

  public String fromHexAddress(String hexAddress) throws BitcoindException, CommunicationException {

    String addressJson = this.rpcClient.execute(QtumCommands.FROM_HEX_ADDRESS, hexAddress);
    return this.rpcClient.getParser().parseString(addressJson);

  }

  public BigDecimal getContractBalance(String contractAddress, String address, Integer decimal)
      throws BitcoindException, CommunicationException {

    String hexAddress = getHexAddress(address);
    String data =
        GET_CONTRACT_BALANCE_CODE + QtumUtils.completeStringTo64(hexAddress, QtumUtils.SIXTY_FOUR);
    List<Object> params = CollectionUtils.asList(contractAddress, data);
    String responseJson = rpcClient.execute(QtumCommands.CALL_CONTRACT, params);
    JSONObject result = JSON.parseObject(responseJson);

    JSONObject executionResult = JSONObject.parseObject(result.get("executionResult").toString());
    String output = JSONObject.parseObject(executionResult.toString()).get("output").toString();

    return QtumUtils.hexToDecimal(output, decimal);
  }


  public String sendContractTx(String contractAddress, String from, BigDecimal amount,
      BigInteger gasLimit, String to, Integer decimal)
      throws BitcoindException, CommunicationException, URISyntaxException, IOException {

    String toHex = QtumUtils.completeStringTo64(getHexAddress(to), QtumUtils.SIXTY_FOUR);
    String amountHex = QtumUtils.decimalToHex(amount, decimal);
    String dataHex = SEND_CONTRACT_TX_CODE + toHex + amountHex;

    String requestJson =
        "{\"method\":\"" + QtumCommands.SEND_TO_CONTRACT + "\",\"params\":[\"" + contractAddress
            + "\",\""
            + dataHex + "\",0," + gasLimit + ", 0.00000040 , " + "\"" + from
            + "\"],\"jsonrpc\":\"1.0\",\"id\":\"" + IDGeneratorUtil.getUUID() + "\"}";

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost();
    httpPost.setEntity(new StringEntity(requestJson, ContentType
        .create(DataFormats.JSON.getMediaType(), Constants.UTF_8)));

    String rpcUser = nodeConfig.getProperty("node.bitcoind.rpc.user");
    String rpcPassword = nodeConfig.getProperty("node.bitcoind.rpc.password");
    String url = nodeConfig.getProperty("node.bitcoind.rpc.protocol") + "://" + nodeConfig
        .getProperty("node.bitcoind.rpc.host") + ":" + nodeConfig
        .getProperty("node.bitcoind.rpc.port");

    String auth = Base64.encodeBase64String((rpcUser + ":" + rpcPassword).getBytes());
    httpPost.addHeader(new BasicHeader("Authorization", "Basic " + auth));
    httpPost.setURI(new URI(url));
    CloseableHttpResponse response = httpClient.execute(httpPost);
    String responseJson = EntityUtils.toString(response.getEntity());
    TxnSendResponse txnSendResponse = JSON.parseObject(responseJson, TxnSendResponse.class);
    httpClient.close();
    return txnSendResponse.getResult().getTxid();

  }

  public List<ContractTransaction> getContractTransactionsSinceBlock(Integer startBlock,
      Integer endBlock, List<String> addresses)
      throws BitcoindException, CommunicationException {

    String addressesJson = new JSONObject(ImmutableMap.of("addresses", addresses)).toJSONString();
    String topicsJson = new JSONObject(ImmutableMap.of("topics",
        Lists.newArrayList("ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef")))
        .toJSONString();
    List<Object> params = CollectionUtils.asList(startBlock, endBlock, addressesJson, topicsJson);
    String responseJson = rpcClient.execute(QtumCommands.SEARCH_LOGS, params);
    List<ContractTransaction> contractTransactions = JSON.parseArray(responseJson).stream()
        .map(object -> JSON.parseObject(object.toString(), ContractTransaction.class)).collect(
            Collectors.toList());
    return contractTransactions;

  }

  public String getNewAddress(String account) throws BitcoindException, CommunicationException {
    String addressJson = rpcClient.execute(Commands.GET_NEW_ADDRESS.getName(), account);
    String address = rpcClient.getParser().parseString(addressJson);
    return address;
  }

  public String dumpPrivKey(String address) throws BitcoindException, CommunicationException {
    String privateKeyJson = rpcClient.execute(Commands.DUMP_PRIV_KEY.getName(), address);
    String privateKey = rpcClient.getParser().parseString(privateKeyJson);
    return privateKey;
  }


  public List<Output> listUnspent(Integer minConfirmations, Integer maxConfirmations,
      ArrayList<String> addresses) throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(minConfirmations, maxConfirmations, addresses);
    String unspentOutputsJson = rpcClient.execute(Commands.LIST_UNSPENT.getName(), params);
    List<Output> unspentOutputs = rpcClient.getMapper().mapToList(unspentOutputsJson,
        Output.class);
    return unspentOutputs;
  }
}
