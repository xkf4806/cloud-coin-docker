package com.ourdax.coindocker.asset.achain.contract;

import com.ourdax.coindocker.asset.achain.AchainBasedRpcProcessor;
import com.ourdax.coindocker.asset.achain.act.AchainProperties;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.achain.common.Constants;
import com.ourdax.coindocker.common.clients.achain.pojo.ActTransaction;
import com.ourdax.coindocker.common.enums.ACTContractEnum;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@Slf4j
public abstract class ACTCONTRACTRpcProcessor extends AchainBasedRpcProcessor {

  @Autowired
  private AchainProperties properties;

  private static final BigDecimal ACT_REMAINING = new BigDecimal("0.1");

  @Override
  public BigDecimal queryBalance() {
    return new RpcCallTemplate<>(() -> {

      ACTContractEnum actAsset = ACTContractEnum.valueOf(getAssetCode().name());

      Long balance = achainClient.getContractBalance(getAccountName(), actAsset.getContract());
      return fromWei(BigInteger.valueOf(balance), actAsset.getDecimal());
    }).execute();
  }

  public List<TransInfo> queryTransInfo(String txId) {

    ActTransaction transaction = new RpcCallTemplate<>(() ->
        achainClient.getContractTransaction(txId)).execute();
    return Optional.of(transaction).map(this::adaptTransInfo).map(Collections::singletonList)
        .orElseGet(Collections::emptyList);
  }

  @Override
  public TransStatus getTransStatus(TransInfo transInfo) {
      return super.getTransStatus(transInfo);
  }

  @Override
  public String getUniformAccount() {
    return getAccountName();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String inAddress) {
    return new RpcCallTemplate<>(() -> {

      ACTContractEnum actAsset = ACTContractEnum.valueOf(getAssetCode().name());

      Long balance = achainClient.getBalance(inAddress);
      return fromWei(BigInteger.valueOf(balance), actAsset.getDecimal());
    }).execute();
  }

  @Override
  public BigDecimal queryBalance(String inAddress) {
    return new RpcCallTemplate<>(() -> {
      ACTContractEnum actAsset = ACTContractEnum.valueOf(getAssetCode().name());
      Long balance = achainClient.getContractBalance(inAddress, actAsset.getContract());
      return fromWei(BigInteger.valueOf(balance), actAsset.getDecimal());
    }).execute();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    return new RpcCallTemplate<>(() -> {

      ACTContractEnum actAsset = ACTContractEnum.valueOf(getAssetCode().name());

      Long balance = achainClient.getBalance(getAccountName());
      return fromWei(BigInteger.valueOf(balance), actAsset.getDecimal());
    }).execute();
  }

  @Override
  protected String doTransfer(RpcTransRequest rpcTransRequest) {
    return new RpcCallTemplate<>(
        () -> {
          ACTContractEnum contractAsset = ACTContractEnum
              .valueOf(rpcTransRequest.getAssetCode().name());
          return achainClient.transferToContract(contractAsset.getContract(), getAccountName(), rpcTransRequest.getTo(),
              AssetCode.ACT.name(),rpcTransRequest.getAmount(), Constants.EXPENSE_CEILING).getEntryId();
        }
    ).execute();
  }

  @Override
  protected void ensureFundSufficient(RpcTransRequest rpcTransRequest) {
    BigDecimal actBalance = queryChainMajorTokenBalance();
    BigDecimal assetBalance = queryBalance();
    boolean sufficient = actBalance.compareTo(ACT_REMAINING) >= 0 &&
        assetBalance.compareTo(rpcTransRequest.getAmount()) >= 0;
    if (!sufficient) {
      String msg = String.format("insufficient fund, act remaining %s, %s remaining %s",
          actBalance, getAssetCode(), assetBalance);
      throw new AssetException(msg);
    }
  }


  @Override
  protected String getAccountName() {
    return properties.getAccountName();
  }

  @Override
  protected String getWalletName() {
    return properties.getWalletName();
  }

  @Override
  protected String getWalletPassword() {
    return properties.getWalletPassword();
  }
}
