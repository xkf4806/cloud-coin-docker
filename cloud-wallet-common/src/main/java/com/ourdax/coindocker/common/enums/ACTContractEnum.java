package com.ourdax.coindocker.common.enums;

/**
 * This class contains tokens developed on achain.
 * Created by zhangjinyang on 2018/5/21.
 */
public enum ACTContractEnum {

  CNT("CON61GJk3hTxWQ9qMDsZy9hTNVFYG8Gja2Fu", 5),
  KCASH("COND41iays8576giHf6M6Yox1DiBrDmgVyzJ", 5);

  private String contract;

  private Integer decimal;

  ACTContractEnum(String contract, Integer decimal) {
    this.contract = contract;
    this.decimal = decimal;
  }

  public static ACTContractEnum fromContract(String contract){

    for (ACTContractEnum coin :  values()){
      if (coin.getContract().equals(contract)){
        return coin;
      }
    }
    return null;
  }

  public static boolean containContract(String contract){

    for (ACTContractEnum coin :  values()){
      if (coin.getContract().equals(contract)){
        return true;
      }
    }
    return false;
  }

  public Integer getDecimal() {
    return decimal;
  }

  public void setDecimal(Integer decimal) {
    this.decimal = decimal;
  }

  public String getContract() {
    return contract;
  }

  public void setContract(String contract) {
    this.contract = contract;
  }


}
