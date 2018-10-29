package com.ourdax.coindocker.asset.etc;

import com.ourdax.coindocker.asset.EthBasedRpcProcessor;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.utils.EthUtils;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

/**
 * Created by zhangjinyang on 2018/1/22.
 */
@Lazy
@Component(value = "ETCRpcProcessor")
@Slf4j
public class ETCRpcProcessor extends EthBasedRpcProcessor {

  @Autowired
  private ETCProperties properties;

  private Admin client;

  @PostConstruct
  public void init() {
    client = Admin.build(new HttpService(properties.getServiceUrl()));  // defaults to http://localhost:8546/
  }

  @Override
  protected Admin getClient() {
    return client;
  }

  @Override
  protected String getDepositAddress() {
    return properties.getDepositAddress();
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ETC;
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    ensureFundSufficient(rpcTransRequest.getAmount());
    unlockAddress(properties.getDepositAddress(), properties.getPassphrase());
  }

  @Override
  protected String doTransfer(String to, BigDecimal amount) {

    return new RpcCallTemplate<>(() -> {
      EthSendTransaction transaction = client
          .ethSendTransaction(org.web3j.protocol.core.methods.request.Transaction
              .createEtherTransaction(properties.getDepositAddress(), null, GAS_PRICE, GAS_LIMIT,
                  to, EthUtils.toWei(amount, ETH_DECIMALS)))
          .send();
      return transaction.getTransactionHash();
    }).execute();
  }


  @Override
  public BigDecimal queryBalance() {
    return getEthBalance(properties.getDepositAddress());
  }

  @Override
  public BigDecimal queryBalance(String account) {
    return getEthBalance(account);
  }

  @Override
  protected int getConfirmThreshold() {
    return properties.getConfirmThreshold();
  }

}
