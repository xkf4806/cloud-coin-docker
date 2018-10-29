package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.exception.ApiCallException;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
public interface AddressGenService {

  Boolean generateAssetAddresses(String asset, Integer number, String accountName,
      String passPhrase) throws ApiCallException;

}
