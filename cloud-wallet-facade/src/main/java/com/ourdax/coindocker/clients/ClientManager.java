package com.ourdax.coindocker.clients;

import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Component
public class ClientManager {

  private Map<String, AssetClient> beansOfTypes;
  @Autowired
  private ApplicationContext applicationContext;
  
  @PostConstruct
  public void init(){
    beansOfTypes = applicationContext.getBeansOfType(AssetClient.class);
  }

  public AssetClient getSpecificInstance(String asset){
    AssetCode assetCode = AssetCode.valueOf(asset);
    for (AssetClient client : beansOfTypes.values()){
      if(assetCode.equals(client.getAssetCode()) || contractOf(asset, client.getAssetCode())){
        return client;
      }
    }
    return null;
  }

  private boolean contractOf(String asset, AssetCode assetCode) {
    AssetCode assetConstant = AssetCode.valueOf(asset);
    if(assetConstant.equals(AssetCode.ERC20) && assetCode.equals(AssetCode.ETH))
      return true;
    if(assetConstant.equals(AssetCode.QRC20) && assetCode.equals(AssetCode.QTUM))
      return true;
    if(assetConstant.equals(AssetCode.ACTCONTRACT) && assetCode.equals(AssetCode.ACT))
      return true;
    return false;
  }

}
