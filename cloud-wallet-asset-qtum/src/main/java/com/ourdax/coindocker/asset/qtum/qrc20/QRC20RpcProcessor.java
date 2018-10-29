package com.ourdax.coindocker.asset.qtum.qrc20;

import com.google.common.collect.Lists;
import com.neemre.btcdcli4j.core.domain.Output;
import com.ourdax.coindocker.asset.qtum.QTUMBasedRpcProcessor;
import com.ourdax.coindocker.asset.qtum.QTUMProperties;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import com.ourdax.coindocker.common.clients.qtum.QtumUtils;
import com.ourdax.coindocker.common.clients.qtum.pojos.ContractTransaction;
import com.ourdax.coindocker.common.clients.qtum.pojos.TransactionReceipt;
import com.ourdax.coindocker.common.enums.QRC20Enum;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;

/**
 * @author think on 9/2/2018
 */
@Slf4j
public abstract class QRC20RpcProcessor extends QTUMBasedRpcProcessor {

  private static final Integer MIN_CONFIRMATIONS = 6;

  private static final Integer MAX_CONFIRMATIONS = 99999999;

  @Autowired
  private QTUMProperties properties;

  protected QRC20Enum qrc20Enum;

  private static final BigDecimal QTUM_REMAINING = new BigDecimal("0.5");

  private static final BigInteger GAS_LIMIT = BigInteger.valueOf(250000);

  @Override
  protected QtumClient getQtumClient() {
    return client;
  }

  @Override
  protected Integer getConfirmThreshold() {
    return properties.getConfirmThreshold();
  }

  @Override
  public BlockTrans queryTransSince(Block block) {

    Integer nextBlockNumber = Integer.parseInt(block.getBlockNumber()) + 1;
    String nextBlockHash = new RpcCallTemplate<>(
        () -> getQtumClient().getBlockHash(Integer.parseInt(block.getBlockNumber()) + 1)).execute();

    List<String> addresses = Lists.newArrayList(qrc20Enum.getContract());

    List<ContractTransaction> contractTransactions = new RpcCallTemplate<>(() ->
        getQtumClient()
            .getContractTransactionsSinceBlock(nextBlockNumber, nextBlockNumber, addresses)
    ).execute();

    List<TransInfo> transInfos = contractTransactions.stream().filter(
        transaction -> (addresses.contains(transaction.getTo())))
        .map(this::adaptTransInfo).collect(Collectors.toList());

    BlockTrans result = new BlockTrans();
    result.setBlock(new SimpleBlock(nextBlockNumber.toString(), nextBlockHash));
    result.setTrans(transInfos);
    return result;
  }

  private TransInfo adaptTransInfo(ContractTransaction transaction) {

    TransInfo transInfo = new TransInfo();
    transInfo.setAmount(QtumUtils.hexToDecimal(transaction.getLog().get(0).getData(),
        qrc20Enum.getDecimal()));
    transInfo.setFee(parseValue(transaction.getGasUsed(), qrc20Enum.getDecimal()));
    String targetHexString = transaction.getLog().get(0).getTopics().get(2);
    String hexTo = QtumUtils
        .completeStringTo64(QtumUtils.trimHexString(targetHexString), QtumUtils.FOURTY);
    String to = new RpcCallTemplate<>(() ->getQtumClient()
        .fromHexAddress(hexTo)).execute();

    transInfo.setTo(to);
    transInfo.setBlockHash(transaction.getBlockHash());
    transInfo.setBlockNumber(transaction.getBlockNumber().toString());
    transInfo.setTxId(transaction.getTransactionHash());
    transInfo.setAssetCode(getAssetCode());

    return transInfo;
  }

  private BigDecimal parseValue(Integer gasUsed, Integer decimal) {

    double pow = Math.pow(10, decimal);
    return new BigDecimal(gasUsed).divide(new BigDecimal(pow));

  }


  public BigDecimal queryBalance() {

    BigDecimal contractBalance = new RpcCallTemplate<>(() ->
        getQtumClient().getContractBalance(qrc20Enum.getContract(),
            properties.getQrc20DepositAddress(), qrc20Enum.getDecimal())
    ).execute();
    return contractBalance;

  }

  @Override
  public List<TransInfo> queryTransInfo(String txId) {

    TransactionReceipt transaction = new RpcCallTemplate<>(
        () -> getQtumClient().getTransactionReceipt(txId)).execute();
    if (transaction == null){
      return Lists.newArrayList();
    }
    ContractTransaction contractTransaction = new ContractTransaction();
    BeanCopier.create(TransactionReceipt.class, ContractTransaction.class, false)
        .copy(transaction, contractTransaction, null);
    return Lists.newArrayList(adaptTransInfo(contractTransaction));

  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String address) {
    return new RpcCallTemplate<>(() -> {
      List<Output> outputs = getQtumClient()
          .listUnspent(MIN_CONFIRMATIONS, MAX_CONFIRMATIONS, Lists.newArrayList(address));
      BigDecimal balance = BigDecimal.ZERO;
      for (Output op : outputs) {
        balance = balance.add(op.getAmount());
      }
      return balance;
    }).execute();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    return queryChainMajorTokenBalance(properties.getQrc20DepositAddress());
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    BigDecimal qtumBalance = queryBalance();
    BigDecimal assetBalance = queryBalance();
    boolean sufficient =
        qtumBalance.compareTo(QTUM_REMAINING) > 0
            && assetBalance.compareTo(rpcTransRequest.getAmount()) > 0;

    if (!sufficient) {
      String msg = String.format("insufficient fund, qtum remaining %s, %s remaining %s",
          qtumBalance, rpcTransRequest.getAssetCode(), assetBalance);
      throw new AssetException(msg);
    }
  }

  @Override
  public RpcTransResponse defaultTransfer(RpcTransRequest request) {
    String txId = new RpcCallTemplate<>(
        () -> getQtumClient()
            .sendContractTx(qrc20Enum.getContract(), properties.getQrc20DepositAddress(),
                request.getAmount(), GAS_LIMIT, request.getTo(), qrc20Enum.getDecimal())).execute();
    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    return response;
  }

  @Override
  public BigDecimal queryBalance(String inAddress) {
    BigDecimal contractBalance = new RpcCallTemplate<>(() ->
        getQtumClient().getContractBalance(qrc20Enum.getContract(),
            inAddress, qrc20Enum.getDecimal())
    ).execute();
    return contractBalance;
  }

  @Override
  public String getUniformAccount() {
    return properties.getQrc20DepositAddress();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {

    String txId = new RpcCallTemplate<>(
        () -> getQtumClient()
            .sendContractTx(qrc20Enum.getContract(), from,
                amount, GAS_LIMIT, to, qrc20Enum.getDecimal())).execute();
    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    return response;
  }

}
