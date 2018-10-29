package com.ourdax.coindocker.common.enums;

/**
 * This class contains token deployed on qtum contract.
 * Created by zhangjinyang on 2018/5/4.
 */
public enum QRC20Enum {

  INK("fe59cbc1704e89a698571413a81f0de9d8f00c69", 9, AssetCode.INK),
  EPC("2e1b8528c07539b5dd9a76f3374adf09f1ab6075", 18, AssetCode.EPC);

  private String contract;

  private Integer decimal;

  private AssetCode assetCode;

  QRC20Enum(String contract, Integer decimal, AssetCode assetCode) {
    this.contract = contract;
    this.decimal = decimal;
    this.assetCode = assetCode;
  }

  public static QRC20Enum getContract(String contract){

    for (QRC20Enum coin :  values()){
      if (coin.getContract().equals(contract)){
        return coin;
      }
    }
    return null;
  }

  public static QRC20Enum fromAssetCode(AssetCode assetCode){

    for (QRC20Enum coin :  values()){
      if (coin.getAssetCode().equals(assetCode)){
        return coin;
      }
    }
    return null;
  }

  public String getContract() {
    return contract;
  }

  public void setContract(String contract) {
    this.contract = contract;
  }

  public Integer getDecimal() {
    return decimal;
  }

  public void setDecimal(Integer decimal) {
    this.decimal = decimal;
  }

  public AssetCode getAssetCode() {
    return assetCode;
  }

  public void setAssetCode(AssetCode assetCode) {
    this.assetCode = assetCode;
  }
}
