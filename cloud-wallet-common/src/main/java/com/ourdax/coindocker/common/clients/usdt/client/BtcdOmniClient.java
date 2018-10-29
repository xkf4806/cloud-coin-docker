package com.ourdax.coindocker.common.clients.usdt.client;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.Commands;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.ClientConfigurator;
import com.neemre.btcdcli4j.core.common.Defaults;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Info;
import com.neemre.btcdcli4j.core.domain.Output;
import com.neemre.btcdcli4j.core.domain.OutputOverview;
import com.neemre.btcdcli4j.core.domain.SignatureResult;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClient;
import com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClientImpl;
import com.neemre.btcdcli4j.core.util.CollectionUtils;
import com.ourdax.coindocker.common.clients.usdt.omni.OmniBalance;
import com.ourdax.coindocker.common.clients.usdt.omni.OmniCommand;
import com.ourdax.coindocker.common.clients.usdt.omni.OmniOutput;
import com.ourdax.coindocker.common.clients.usdt.omni.OmniTransaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangjinyang on 2018/3/7.
 */
public class BtcdOmniClient {

  private static final Logger LOG = LoggerFactory.getLogger(BtcdOmniClient.class);

  private static final Integer USDT_PROPERTY_Id = 31;

  private ClientConfigurator configurator;

  private JsonRpcClient rpcClient;

  public BtcdOmniClient(CloseableHttpClient httpProvider, Properties nodeConfig)
      throws BitcoindException, CommunicationException {
    LOG.info(">> initialize(..): initiating the 'omnicoind' core wrapper");
    configurator = new ClientConfigurator();
    rpcClient = new JsonRpcClientImpl(configurator.checkHttpProvider(httpProvider),
        configurator.checkNodeConfig(nodeConfig));
    configurator.checkNodeVersion(getInfo().getVersion());
    configurator.checkNodeHealth((Block) getBlock(getBestBlockHash(), true));
  }

  public String omniSend(String fromAddress, String toAddress, int propertyId, BigDecimal amount) throws BitcoindException, CommunicationException {
    amount = amount.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
    List<Object> params = CollectionUtils
        .asList(fromAddress, toAddress, 31, amount.toString());
    String transactionIdJson = rpcClient.execute(OmniCommand.OMNI_SEND.getName(), params);
    System.out.println(transactionIdJson);
    String transactionId = rpcClient.getParser().parseString(transactionIdJson);
    return transactionId;
  }

  public String omniFundedSend(String fromAddress, String toAddress, BigDecimal amount, String feeAddress) throws BitcoindException, CommunicationException {
    amount = amount.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
    List<Object> params = CollectionUtils
        .asList(fromAddress, toAddress, USDT_PROPERTY_Id, amount.toString(), feeAddress);
    String transactionIdJson = rpcClient.execute(OmniCommand.OMNI_FUNDED_SEND.getName(), params);
    System.out.println(transactionIdJson);
    String transactionId = rpcClient.getParser().parseString(transactionIdJson);
    return transactionId;
  }

  public OmniTransaction getOmniTransaction(String txId) throws BitcoindException,
      CommunicationException {
    String transactionJson = rpcClient.execute(OmniCommand.OMNI_GET_TRANSACTION.getName(), txId);
    OmniTransaction transaction = rpcClient.getMapper().mapToEntity(transactionJson,
        OmniTransaction.class);
    return transaction;
  }

  public OmniBalance omniGetBalance(String address, Integer propertyid) throws BitcoindException,
      CommunicationException {

    List<Object> params = CollectionUtils.asList(address, propertyid);
    String balanceJson = rpcClient.execute(OmniCommand.OMNI_GETBALANCE.getName(), params);
    OmniBalance omniBalance = rpcClient.getMapper().mapToEntity(balanceJson,
        OmniBalance.class);
    return omniBalance;
  }

  public BigDecimal getBalance(String account) throws BitcoindException, CommunicationException {
    String balanceJson = rpcClient.execute(Commands.GET_BALANCE.getName(), account);
    BigDecimal balance = rpcClient.getParser().parseBigDecimal(balanceJson);
    return balance;
  }

  /**
   * list all Omni transactions in a block
   * @param blockNum
   * @return
   * @throws BitcoindException
   * @throws CommunicationException
   */
  public List<String> omniListBlockTransactions(Long blockNum) throws BitcoindException,
      CommunicationException {

    List<Object> params = CollectionUtils.asList(blockNum);
    String blockTransactionsJson = rpcClient.execute(OmniCommand.OMNI_LISTBLOCKTRANS.getName(), params);
    List<String> txIdList = rpcClient.getMapper().mapToEntity(blockTransactionsJson,
        List.class);
    return txIdList;
  }

  public Info getInfo() throws BitcoindException, CommunicationException {
    String infoJson = rpcClient.execute(Commands.GET_INFO.getName());
    Info info = rpcClient.getMapper().mapToEntity(infoJson, Info.class);
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

  public List<Output> listUnspent(Integer minConfirmations) throws BitcoindException,
      CommunicationException {
    String unspentOutputsJson = rpcClient.execute(Commands.LIST_UNSPENT.getName(),
        minConfirmations);
    List<Output> unspentOutputs = rpcClient.getMapper().mapToList(unspentOutputsJson,
        Output.class);
    return unspentOutputs;
  }

  public List<Output> listUnspent(Integer minConfirmations, Integer maxConfirmations,
      List<String> addresses) throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(minConfirmations, maxConfirmations, addresses);
    String unspentOutputsJson = rpcClient.execute(Commands.LIST_UNSPENT.getName(), params);
    List<Output> unspentOutputs = rpcClient.getMapper().mapToList(unspentOutputsJson,
        Output.class);
    return unspentOutputs;
  }

  public String omniCreatepayloadSimplesend(Integer propertyid, BigDecimal amount)
      throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(propertyid, amount.toString());
    String hexPayLoadJson = rpcClient.execute(OmniCommand.OMNI_CREATEPAYLOAD_SIMPLESEND.getName(), params);
    String hexPayLoad = rpcClient.getParser().parseString(hexPayLoadJson);
    return hexPayLoad;
  }

  public String createRawTransaction(List<OutputOverview> outputs,
      Map<String, BigDecimal> toAddresses) throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(outputs, toAddresses);
    String hexTransactionJson = rpcClient.execute(Commands.CREATE_RAW_TRANSACTION.getName(),
        params);
    String hexTransaction = rpcClient.getParser().parseString(hexTransactionJson);
    return hexTransaction;
  }


  public String omniCreaterawtxOpreturn(String hexRawTransaction, String hexPayload)
      throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(hexRawTransaction, hexPayload);
    String resultJson = rpcClient.execute(OmniCommand.OMNI_CREATERAWTX_OPRETURN.getName(), params);
    String result = rpcClient.getParser().parseString(resultJson);
    return result;
  }

  public String omniCreaterawtxReference(String attachPayLoadResult, String receiver)
      throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(attachPayLoadResult, receiver);
    String resultJson = rpcClient.execute(OmniCommand.OMNI_CREATERAWTX_REFERENCE.getName(), params);
    String result = rpcClient.getParser().parseString(resultJson);
    return result;
  }


  public String omniCreaterawtxChange(String rawTxReferenceResult, List<OmniOutput> outputs, String s,
      BigDecimal bigDecimal) throws BitcoindException, CommunicationException {

    List<Object> params = CollectionUtils.asList(rawTxReferenceResult, outputs, s,  bigDecimal.toString());
    String resultJson = rpcClient.execute(OmniCommand.OMNI_CREATERAWTX_CHANGE.getName(), params);
    String result = rpcClient.getParser().parseString(resultJson);
    return result;
  }

  public SignatureResult signRawTransaction(String hexTransaction) throws BitcoindException,
      CommunicationException {
    String signatureResultJson = rpcClient.execute(Commands.SIGN_RAW_TRANSACTION.getName(),
        hexTransaction);
    SignatureResult signatureResult = rpcClient.getMapper().mapToEntity(signatureResultJson,
        SignatureResult.class);
    return signatureResult;
  }

  public SignatureResult signRawTransaction(String hexTransaction, List<Output> outputs)
      throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(hexTransaction, outputs);
    String signatureResultJson = rpcClient.execute(Commands.SIGN_RAW_TRANSACTION.getName(),
        params);
    SignatureResult signatureResult = rpcClient.getMapper().mapToEntity(signatureResultJson,
        SignatureResult.class);
    return signatureResult;
  }

  public SignatureResult signRawTransaction(String hexTransaction, List<Output> outputs,
      List<String> privateKeys) throws BitcoindException, CommunicationException {
    List<Object> params = CollectionUtils.asList(hexTransaction, outputs, privateKeys);
    String signatureResultJson = rpcClient.execute(Commands.SIGN_RAW_TRANSACTION.getName(),
        params);
    SignatureResult signatureResult = rpcClient.getMapper().mapToEntity(signatureResultJson,
        SignatureResult.class);
    return signatureResult;
  }
}
