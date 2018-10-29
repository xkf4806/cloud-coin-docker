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
public class ETCClient implements AssetClient<Admin> {

  @Value("${etc.rpcServiceUrl}")
  private String rpcServiceUrl;

  private volatile Admin etcClient;

  public Admin getClientInstance() {
    if (etcClient == null){
      synchronized (ETCClient.class){
        if (etcClient ==  null){
          etcClient = Admin.build(new HttpService(rpcServiceUrl));
        }
      }
    }
    return etcClient;
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ETC;
  }
}
