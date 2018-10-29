package com.ourdax.coindocker.asset.eth;

import com.alibaba.fastjson.JSON;
import com.ourdax.coindocker.asset.EthBasedRpcProcessor;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.utils.EthUtils;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

/**
 * @author think on 13/1/2018
 */
@Component(value = "ETHRpcProcessor")
@Lazy
@Slf4j
public class ETHRpcProcessor extends EthBasedRpcProcessor {

  @Autowired
  private Admin client;

  @Value("${eth.own.depositAddress}")
  protected String depositAddress;

  @Value("${eth.own.passphrase}")
  private String passphrase;

  @Override
  protected Admin getClient() {
    return client;
  }

  @Override
  protected String getDepositAddress() {
    return depositAddress;
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ETH;
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    ensureFundSufficient(rpcTransRequest.getAmount());
    unlockAddress(depositAddress, passphrase);
  }

  @Override
  protected String doTransfer(String to, BigDecimal amount) {

    return new RpcCallTemplate<>(
        () -> {
          EthSendTransaction send = client
              .ethSendTransaction(org.web3j.protocol.core.methods.request.Transaction
                  .createEtherTransaction(depositAddress, null, GAS_PRICE, GAS_LIMIT, to, EthUtils
                      .toWei(amount, ETH_DECIMALS)))
              .send();
          log.info("result about transferring "+ getAssetCode() + "to " + to + ": " +
              JSON.toJSONString(send));
          return send.getTransactionHash();
        }
    ).execute();
  }


  @Override
  public BigDecimal queryBalance() {
    return getEthBalance(depositAddress);
  }

  @Override
  public BigDecimal queryBalance(String account) {
    return getEthBalance(account);
  }

  @Override
  protected int getConfirmThreshold() {
    return ETH_CONFIRM_NUM;
  }


}
