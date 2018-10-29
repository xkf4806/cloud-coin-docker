package com.ourdax.coindocker.clients;

import com.ourdax.coindocker.common.enums.AssetCode;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
public interface AssetClient<T> {

   public <T> T getClientInstance();

   AssetCode getAssetCode();

}
