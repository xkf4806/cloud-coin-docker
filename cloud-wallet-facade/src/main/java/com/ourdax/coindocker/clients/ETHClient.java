package com.ourdax.coindocker.clients;

import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Configuration
public class ETHClient implements AssetClient<Admin> {

  @Value("${eth.rpcServiceUrl}")
  private String rpcServiceUrl;

  private volatile Admin ethClient;

  public Admin getClientInstance() {
    if (ethClient == null){
      synchronized (ETHClient.class){
        if (ethClient ==  null){
          ethClient = Admin.build(new HttpService(rpcServiceUrl));
        }
      }
    }
    return ethClient;
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ETH;
  }
}
