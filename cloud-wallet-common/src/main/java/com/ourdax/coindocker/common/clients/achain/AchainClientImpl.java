package com.ourdax.coindocker.common.clients.achain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ourdax.coindocker.common.clients.achain.common.Commands;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.client.JsonRpcClient;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.client.JsonRpcClientImpl;
import com.ourdax.coindocker.common.clients.achain.pojo.AchainBlock;
import com.ourdax.coindocker.common.clients.achain.pojo.ActTransaction;
import com.ourdax.coindocker.common.clients.achain.pojo.ContractBalance;
import com.ourdax.coindocker.common.clients.achain.pojo.ContractCoinType;
import com.ourdax.coindocker.common.clients.achain.pojo.ContractResult;
import com.ourdax.coindocker.common.clients.achain.pojo.ContractTransaction;
import com.ourdax.coindocker.common.clients.achain.pojo.PrettyTransaction;
import com.ourdax.coindocker.common.clients.achain.pojo.Transaction;
import com.ourdax.coindocker.common.clients.achain.pojo.TrxType;
import com.ourdax.coindocker.common.clients.achain.pojo.WalletInfo;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author think on 30/1/2018
 */
public class AchainClientImpl implements AchainClient {


  private static final Logger log = LoggerFactory.getLogger(AchainClientImpl.class);

  private final JsonRpcClient rpcClient;

  public AchainClientImpl(CloseableHttpClient httpClient, Properties clientConfig) {
    checkConfig(clientConfig);
    rpcClient = new JsonRpcClientImpl(httpClient, clientConfig);
  }

  public AchainClientImpl(Properties clientConfig) {
    this(HttpClients.createDefault(), clientConfig);
  }

  private void checkConfig(Properties clientConfig) {
  }

  @Override
  public Long getBlockCount() throws AchainClientException {
    String json = rpcClient.execute(Commands.BLOCKCHAIN_GET_BLOCK_COUNT);
    return rpcClient.getParser().parseLong(json);
  }

  @Override
  public AchainBlock getBlockInfo(Long blockNumber) throws AchainClientException {
    List<String> params = Arrays.asList(String.valueOf(blockNumber));
    String json = rpcClient.execute(Commands.BLOCKCHAIN_GET_BLOCK, params);
    return rpcClient.getMapper().mapToEntity(json, AchainBlock.class);

  }



  @Override
  @SuppressWarnings("unchecked")
  public Long getBalance(String accountName) throws AchainClientException {
    List<String> params = Collections.singletonList(accountName);
    String json = rpcClient.execute(Commands.WALLET_ACCOUNT_BALANCE, params);
    JSONArray jsonArray = rpcClient.getMapper().mapToEntity(json, JSONArray.class);
    List<Object> accountBalances = (List<Object>)jsonArray.get(0);
    Integer balance = (Integer)((List<Object>)((List<Object>)accountBalances.get(1)).get(0)).get(1);
    return Long.valueOf(balance);
  }

  @Override
  public Transaction getWalletTransaction(String walletName, String txHash) throws AchainClientException {
    List<String> params = Collections.singletonList(txHash);
    String json = rpcClient.execute(Commands.WALLET_GET_TRANSACTION, params);
    return rpcClient.getMapper().mapToEntity(json, Transaction.class);
  }

  @Override
  public PrettyTransaction getPrettyTransaction(String txHash) throws AchainClientException {
    List<String> params = Arrays.asList(txHash, "true");
    String json = rpcClient.execute("blockchain_get_pretty_transaction", params);
    return rpcClient.getMapper().mapToEntity(json, PrettyTransaction.class);
  }

  @Override
  public ContractResult getContractResult(String txHash) throws AchainClientException {
    List<String> params = Collections.singletonList(txHash);
    String json = rpcClient.execute("blockchain_get_contract_result", params);
    return rpcClient.getMapper().mapToEntity(json, ContractResult.class);
  }

  @Override
  public WalletInfo getWalletInfo(String wallet) throws AchainClientException {
    List<String> params = Collections.singletonList(wallet);
    String json = rpcClient.execute("wallet_get_info", params);
    return rpcClient.getMapper().mapToEntity(json, WalletInfo.class);
  }

  @Override
  public void openWallet(String wallet) throws AchainClientException {
    List<String> params = Collections.singletonList(wallet);
    rpcClient.execute("wallet_open", params);
  }

  @Override
  public void unlockWallet(Long timeout, String password) throws AchainClientException {
    List<String> params = Arrays.asList(String.valueOf(timeout), password);
    rpcClient.execute(Commands.WALLET_UNLOCK, params);
  }

  @Override
  public Transaction transferToAddress(String fromAccount, String toAddress, String assetSymbol,
      BigDecimal amount, String memo, String strategy)
      throws AchainClientException {
    List<String> params = Arrays.asList(
        String.valueOf(amount), assetSymbol, fromAccount, toAddress, memo, strategy);
    String json = rpcClient.execute(Commands.WALLET_TRANSFER_TO_ADDRESS, params);
    return rpcClient.getMapper().mapToEntity(json, Transaction.class);
  }

  @Override
  public Transaction transferToAddress(String fromAccount, String toAddress, String assetSymbol,
      BigDecimal amount)
      throws AchainClientException {
    List<String> params = Arrays.asList(
        String.valueOf(amount), assetSymbol, fromAccount, toAddress);
    String json = rpcClient.execute(Commands.WALLET_TRANSFER_TO_ADDRESS, params);
    return rpcClient.getMapper().mapToEntity(json, Transaction.class);
  }


  @Override
  public Long getContractBalance(String account, String contractId) throws AchainClientException {
    List<String> params = Arrays.asList(
        contractId, account, "balanceOf", StringUtils.EMPTY);
    String json = rpcClient.execute("call_contract_local_emit", params);
    List<ContractBalance> contractBalance = rpcClient.getMapper().mapToList(json, ContractBalance.class);
    return contractBalance.get(0).getBalance();
  }

  @Override
  public ContractTransaction callContract(String contractId, String from, String method,
      String methodParam, String assetSymbol, BigDecimal ceilingFee)
      throws AchainClientException {
    List<String> params = Arrays.asList(
        contractId, from, method, methodParam, assetSymbol, String.valueOf(ceilingFee));
    String json = rpcClient.execute(Commands.CALL_CONTRACT, params);
    return rpcClient.getMapper().mapToEntity(json, ContractTransaction.class);
  }

  @Override
  public ContractTransaction transferToContract(String contractId, String fromAccount,
      String toAddress, String assetSymbol, BigDecimal amount, BigDecimal ceilingFee)
      throws AchainClientException {
    String transferParam = new StringJoiner("|")
        .add(toAddress)
        .add(String.valueOf(amount))
        .toString();
    return callContract(contractId, fromAccount, "transfer_to", transferParam, assetSymbol, ceilingFee);
  }

  @Override
  public ActTransaction getTransaction(String txHash) throws AchainClientException {
    List<String> params = Collections.singletonList(txHash);
    String json = rpcClient.execute("blockchain_get_transaction", params);
    return parseActTransaction(json);
  }

  @Override
  public List<ActTransaction> getTransactions(Long blockNum) throws AchainClientException {
    List<String> params = Arrays.asList(String.valueOf(blockNum));
    String json = rpcClient.execute("blockchain_get_block_transactions", params);
    JSONArray transactions = JSONArray.parseArray(json);

    List<ActTransaction> result = Lists.newArrayList();
    for (int i = 0; i < transactions.size(); i++) {
      result.add(parseActTransaction(transactions.getString(i)));
    }
    return result;
  }

  @Override
  public ActTransaction getContractTransaction(String txHash) throws AchainClientException {
    ContractResult contractResult = getContractResult(txHash);
    return getTransaction(contractResult.getTrxId());
  }


  // todo, this method makes me crazy, and should be refactored soon.
  private ActTransaction parseActTransaction(String json)
      throws AchainClientException {

    JSONArray jsonArray = JSONArray.parseArray(json);
    ActTransaction actTransaction = new ActTransaction();

    String trxId = jsonArray.getString(0);
    actTransaction.setTrxId(trxId);
    String firstOpType = jsonArray.getJSONObject(1).getJSONObject("trx").getJSONArray("operations")
            .getJSONObject(0).getString("type");

    String alpAccount =
        jsonArray.getJSONObject(1).getJSONObject("trx").getString("alp_account");
    actTransaction.setSubAddress(alpAccount);
    JSONObject createTaskJson;
    actTransaction.setCoinType("ACT");
    if ("transaction_op_type".equals(firstOpType)) {
      String result_trx_id =
          jsonArray.getJSONObject(1).getJSONObject("trx").getString("result_trx_id");
      String queryId = StringUtils.isEmpty(result_trx_id) ? trxId : result_trx_id;
      String resultSignee = rpcClient.execute("blockchain_get_pretty_contract_transaction", Collections.singletonList(queryId));
      createTaskJson = JSONObject.parseObject(resultSignee);
      actTransaction.setExtraTrxId(StringUtils.isEmpty(result_trx_id) ? trxId : result_trx_id);
      actTransaction.setTrxId(createTaskJson.getString("orig_trx_id"));

      JSONObject temp = createTaskJson.getJSONObject("to_contract_ledger_entry");
      actTransaction.setFromAddr(temp.getString("from_account"));
      actTransaction.setFromAcct(temp.getString("from_account_name"));
      actTransaction.setContractId(temp.getString("to_account"));
      actTransaction.setToAcct("");
      actTransaction.setToAddr("");
      actTransaction.setAmount(temp.getJSONObject("amount").getLong("amount"));
      actTransaction.setFee(temp.getJSONObject("fee").getInteger("amount"));
      actTransaction.setTrxTime(dealTime(createTaskJson.getString("timestamp")));
      actTransaction.setMemo(temp.getString("memo"));

      if ("false".equals(createTaskJson.getString("is_completed"))) {
        actTransaction.setIsCompleted((byte) 0);
      }
      JSONArray reserved = createTaskJson.getJSONArray("reserved");
      actTransaction.setCalledAbi(reserved.size() >= 1 ? reserved.getString(0) : null);
      actTransaction.setAbiParams(reserved.size() > 1 ? reserved.getString(1) : null);
//      JSONArray jsonArray1 = createTaskJson.getJSONArray("from_contract_ledger_entries");
      int trx_type = createTaskJson.getInteger("trx_type");

      Long blockNum = createTaskJson.getLong("block_num");
      JSONObject jsonObject = getEvent(blockNum, trxId);
      actTransaction.setEventType(jsonObject.getString("event_type"));
      actTransaction.setEventParam(jsonObject.getString("event_param"));

      if (trx_type == TrxType.TRX_TYPE_CALL_CONTRACT.getIntKey() &&
          actTransaction.getCalledAbi().contains(ContractCoinType.COIN_TRANSFER_COIN.getValue())) {
        log.info("ActBrowserServiceImpl|saveActBlock|[actTransaction={}]", actTransaction);
        String[] params = actTransaction.getAbiParams().split("\\|");
        String userAddress = params[0];
        if (userAddress.length() > 50) {
          actTransaction.setSubAddress(userAddress);
          userAddress = userAddress.substring(0, userAddress.length() - 32);
        }
        actTransaction.setToAddr(userAddress);
        if (!StringUtils.isEmpty(actTransaction.getCalledAbi()) &&
            StringUtils.isNotEmpty(actTransaction.getEventType()) &&
            actTransaction.getEventType().contains("transfer_to_success")) {
          boolean flag = true;
          for (int i = 1; i < params.length; i++) {
            log.info("getTransactions|gettempp[tempp={}]",params.length >= 2 ? params[i] : "");
            if (!StringUtils.isEmpty(params[i])) {
              if (flag) {
                String tempp = params.length >= 2 ? params[i] : "0";
                try {
                  Double d = Double.parseDouble(tempp);
                  actTransaction.setAmount(new BigDecimal(d < 0 ? "0" : d.toString()).multiply(new BigDecimal(100000)).longValue());
                } catch (Exception e) {
                  log.info("getTransactions|gettempp[tempp={}]", params.length >= 2 ? params[i] : "");
                  actTransaction.setMemo("0");
                }
                flag = false;
              }else {
                actTransaction.setMemo(params[i]);
              }
            }
          }
        }
      }

    } else {
      String resultSignee = rpcClient.execute( "blockchain_get_pretty_transaction", Collections.singletonList(trxId));
      createTaskJson = JSONObject.parseObject(resultSignee);
      JSONObject temp = (JSONObject) createTaskJson.getJSONArray("ledger_entries").get(0);
      actTransaction.setFromAddr(temp.getString("from_account"));
      actTransaction.setFromAcct(temp.getString("from_account_name"));
      actTransaction.setToAcct(temp.getString("to_account_name"));
      actTransaction.setToAddr(temp.getString("to_account"));
      actTransaction.setAmount(temp.getJSONObject("amount").getLong("amount"));
      actTransaction.setFee(createTaskJson.getJSONObject("fee").getInteger("amount"));
      actTransaction.setTrxTime(dealTime(createTaskJson.getString("timestamp")));
      actTransaction.setMemo(temp.getString("memo"));
      actTransaction.setIsCompleted((byte) 0);
    }

    actTransaction.setBlockNum(createTaskJson.getLong("block_num"));
    actTransaction.setBlockPosition(createTaskJson.getInteger("block_position"));
    actTransaction.setTrxType(createTaskJson.getInteger("trx_type"));

    return actTransaction;
  }

  private JSONObject getEvent(Long blockNum, String trxId)
      throws AchainClientException {
    List<String> params = Arrays.asList(String.valueOf(blockNum), trxId);
    String resultEvent = rpcClient.execute("blockchain_get_events", params);
    if (StringUtils.isEmpty(resultEvent)) {
      return new JSONObject();
    }
    JSONArray jsonArray = JSONArray.parseArray(resultEvent);
    JSONObject result = new JSONObject();
    if (null != jsonArray && jsonArray.size() > 0) {
      StringBuffer eventType = new StringBuffer();
      StringBuffer eventParam = new StringBuffer();
      jsonArray.forEach(json ->{
        JSONObject jso = (JSONObject) json;
        eventType.append(eventType.length() > 0 ? "|" : "").append(jso.getString("event_type"));
        eventParam.append(eventParam.length() > 0 ? "|" : "").append(jso.getString("event_param"));
      });
      result.put("event_type",eventType);
      result.put("event_param",eventParam);
    }
    return result;
  }


  private Date dealTime(String timestamp) {
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      return format.parse(timestamp);
    } catch (ParseException e) {
      log.error("dealTime|error|", e);
      return null;
    }
  }

}
