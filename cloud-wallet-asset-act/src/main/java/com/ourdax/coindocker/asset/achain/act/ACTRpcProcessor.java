package com.ourdax.coindocker.asset.achain.act;

import com.ourdax.coindocker.asset.achain.AchainBasedRpcProcessor;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 13/1/2018
 */
@Component(value = "ACTRpcProcessor")
@Lazy
@Slf4j
public class ACTRpcProcessor extends AchainBasedRpcProcessor {

  @Autowired
  private AchainProperties actProperties;

  @Override
  protected void ensureFundSufficient(RpcTransRequest rpcTransRequest) {
    BigDecimal actBalance = queryBalance();
    boolean sufficient = actBalance.compareTo(rpcTransRequest.getAmount()) >= 0;
    if (!sufficient) {
      throw new AssetException(String.format("Insufficient fund, act remaining %s", actBalance));
    }
  }

  @Override
  protected String getAccountName() {
    return actProperties.getAccountName();
  }

  @Override
  protected String getWalletName() {
    return actProperties.getWalletName();
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ACT;
  }

  @Override
  protected String getWalletPassword() {
    return actProperties.getWalletPassword();
  }

  @Override
  protected String doTransfer(RpcTransRequest request) {
    return new RpcCallTemplate<>(
        () -> achainClient
            .transferToAddress(getAccountName(), request.getTo(), getAssetCode().name(),
                request.getAmount()).getEntryId()
    ).execute();
  }

  @Override
  public String getUniformAccount() {
    return getAccountName();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
    String txId = new RpcCallTemplate<>(
        () -> achainClient
            .transferToAddress(from, to, getAssetCode().name(),
                amount).getEntryId()
    ).execute();

    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    log.info("{} transfer from {} to {} successfully, txId: {}", getAssetCode(),
        from, to, txId);
    return response;
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String inAddress) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    throw new UnsupportedOperationException();
  }

}
