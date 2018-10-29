package com.ourdax.coindocker.common.enums;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * 将币种进行分类
 * Created by zhangjinyang on 2018/7/19.
 */
public class TypesOfAsset {

  public static final List<AssetCode> COLLECT_BY_ACCOUNT_SERIES = Lists
      .newArrayList(AssetCode.BTC, AssetCode.LTC, AssetCode.BCH, AssetCode.ACT);

  public static final List<AssetCode> COLLECT_BY_ADDRESS_SERIES = Lists
      .newArrayList(AssetCode.ETH, AssetCode.ETC, AssetCode.USDT, AssetCode.OMG);

}
