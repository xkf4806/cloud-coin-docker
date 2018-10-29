package com.ourdax.coindocker.trans;

import com.ourdax.coindocker.AssetCodeAware;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.domain.TransferOut;
import java.util.List;

/**
 * @author think on 12/1/2018
 */
public interface TransHandler extends AssetCodeAware {

  /**
   * 接收新交易
   */
  void receiveNewTrans(AssetCode assetCode, TransInfo transInfo);

  /**
   * 转出
   */
  void transfer(TransferOut transferOut);

  void transfer(List<TransferOut> transferOuts);

  /**
   * 转出查询
   */
  void queryTransferOut(TransferOut transferOut);

  /**
   * 转入查询
   */
  void queryTransferIn(TransferIn transferIn);
}
