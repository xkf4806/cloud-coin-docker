package com.ourdax.coindocker.asset.qtum;

import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "QTUMRpcProcessor")
public class QTUMRpcProcessor extends QTUMBasedRpcProcessor {

  @Autowired
  private QTUMProperties properties;

  @Override
  protected QtumClient getQtumClient() {
    return client;
  }

  @Override
  protected Integer getConfirmThreshold() {
    return properties.getConfirmThreshold();
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.QTUM;
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getUniformAccount() {
    return properties.getDepositAccount();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
    return null;
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String inAddress) {
    throw new UnsupportedOperationException();
  }

}
