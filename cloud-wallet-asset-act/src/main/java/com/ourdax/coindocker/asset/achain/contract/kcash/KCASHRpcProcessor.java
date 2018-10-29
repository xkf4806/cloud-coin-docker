package com.ourdax.coindocker.asset.achain.contract.kcash;

import com.ourdax.coindocker.asset.achain.AchainBasedRpcProcessor;
import com.ourdax.coindocker.asset.achain.contract.ACTCONTRACTRpcProcessor;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.achain.common.Constants;
import com.ourdax.coindocker.common.clients.achain.pojo.ActTransaction;
import com.ourdax.coindocker.common.enums.ACTContractEnum;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@Component(value = "KCASHRpcProcessor")
@Lazy
@Slf4j
public class KCASHRpcProcessor extends ACTCONTRACTRpcProcessor {


  @Override
  public AssetCode getAssetCode() {
    return AssetCode.KCASH;
  }
}
