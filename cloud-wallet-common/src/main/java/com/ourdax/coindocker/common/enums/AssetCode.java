package com.ourdax.coindocker.common.enums;


import static java.util.EnumSet.noneOf;
import static java.util.EnumSet.of;

import java.util.Set;

public enum AssetCode {

  ETH,
  ETC,
  EOS(of(Characteristic.ERC20)),
  GTO(of(Characteristic.ERC20)),
  SALT(of(Characteristic.ERC20)),
  STORJ(of(Characteristic.ERC20)),
  WTC(of(Characteristic.ERC20)),
  APPC(of(Characteristic.ERC20)),
  TOPC(of(Characteristic.ERC20)),
  OMG(of(Characteristic.ERC20)),

  LRC(of(Characteristic.ERC20)),
  MANA(of(Characteristic.ERC20)),
  STQ(of(Characteristic.ERC20)),
  NULS(of(Characteristic.ERC20)),
  KNC(of(Characteristic.ERC20)),
  REP(of(Characteristic.ERC20)),
  ZRX(of(Characteristic.ERC20)),
  GNT(of(Characteristic.ERC20)),
  LINK(of(Characteristic.ERC20)),
  PAY(of(Characteristic.ERC20)),
  PLC(of(Characteristic.ERC20)),
  ACT,

  SSC(of(Characteristic.ACTCONTRACT)),
  LET(of(Characteristic.ACTCONTRACT)),
  KCASH(of(Characteristic.ACTCONTRACT)),
  CNT(of(Characteristic.ACTCONTRACT)),
  SSP(of(Characteristic.ACTCONTRACT)),
  BTG,
  BTC(of(Characteristic.BATCH_TRANSFER)),
  BCH(of(Characteristic.BATCH_TRANSFER)),
  LTC(of(Characteristic.BATCH_TRANSFER)),
  DOGE(of(Characteristic.BATCH_TRANSFER)),
  USDT,
  QTUM,
  INK(of(Characteristic.QRC20)),
  EPC(of(Characteristic.QRC20)),
  HSR, SWT,

  ERC20(of(Characteristic.CONTRACT)),
  ACTCONTRACT(of(Characteristic.CONTRACT)),
  QRC20(of(Characteristic.CONTRACT));


  private Set<Characteristic> characteristics;

  AssetCode(Set<Characteristic> characteristics) {
    this.characteristics = characteristics;
  }
  AssetCode() {
    this(noneOf(Characteristic.class));
  }

  public boolean supportBatchTransfer() {
    return characteristics.contains(Characteristic.BATCH_TRANSFER);
  }

  public boolean isERC20() {
    return characteristics.contains(Characteristic.ERC20);
  }

  public boolean isQRC20() {
    return characteristics.contains(Characteristic.QRC20);
  }

  public boolean isActContract() {
    return characteristics.contains(Characteristic.ACTCONTRACT);
  }

  public boolean isContract() {
    return characteristics.contains(Characteristic.CONTRACT);
  }

  private enum Characteristic {
    BATCH_TRANSFER, ERC20, ACTCONTRACT, QRC20, CONTRACT
  }



}
