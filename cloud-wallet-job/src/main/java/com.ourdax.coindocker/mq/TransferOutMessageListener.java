package com.ourdax.coindocker.mq;

import static com.google.common.base.MoreObjects.firstNonNull;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.AssetStatus;
import com.ourdax.coindocker.domain.TransferOut;
import com.ourdax.coindocker.domain.TransferOut.WithdrawStatus;
import com.ourdax.coindocker.mq.messages.TransferOutRequest;
import com.ourdax.coindocker.mq.messages.TransferResult;
import com.ourdax.coindocker.service.NotifyService;
import com.ourdax.coindocker.service.TransferOutService;
import com.ourdax.coindocker.utils.SpringContextHelper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author think on 13/1/2018
 */
@Slf4j
public class TransferOutMessageListener extends AbstractAssetMessageListener<TransferOutRequest> {

  private AssetCode assetCode;

  public void setAssetCode(AssetCode assetCode) {
    this.assetCode = assetCode;
  }

  @Override
  public Class<TransferOutRequest> getMessageType() {
    return TransferOutRequest.class;
  }

  @Override
  public AssetCode getAssetCode() {
    return assetCode;
  }

  @Override
  protected void processMessage(TransferOutRequest request, String assetCode) {
    log.info("Receive transfer out req message, content: {}", request);
    AssetCode assetCodeThis = getAssetCode();
    /**对合约代币类型的适配*/
    if (assetCodeThis.equals(AssetCode.QRC20)) {
      assetCodeThis = AssetCode.valueOf(assetCode.toUpperCase());
    } else if (assetCodeThis.equals(AssetCode.ACTCONTRACT)) {
      assetCodeThis = AssetCode.valueOf(assetCode.toUpperCase());
    }
    TransferOut transferOut = saveTransferOutRequest(assetCodeThis, request);
    sendConfirmMessage(transferOut);
  }

  private void sendConfirmMessage(TransferOut transferOut) {
    TransferResult transferResult = new TransferResult();
    transferResult.setAssetCode(transferOut.getAssetCode());
    transferResult.setToWallet(transferOut.getToCoinAddress());
    transferResult.setInnerOrderId(transferOut.getInnerOrderNo());
    transferResult.setOrderId(genOrderId(
        assetCode.isContract() ? AssetCode.valueOf(transferOut.getAssetCode()) : assetCode,
        transferOut.getId()));
    transferResult.setAmount(transferOut.getAmount());
    transferResult.setAssetStatus(AssetStatus.CONFIRM.name());

    NotifyService notifyService = SpringContextHelper.getBean(NotifyService.class);
    notifyService.sendTransferOutNotification(assetCode, transferResult);
  }

  String genOrderId(AssetCode assetCode, int id) {
    return assetCode + "_" + id;
  }

  protected TransferOut saveTransferOutRequest(AssetCode assetCode, TransferOutRequest request) {
    TransferOut transferOut = new TransferOut();
    transferOut.setAssetCode(assetCode.name());
    transferOut.setAmount(request.getAmount());
    transferOut.setTxFee(firstNonNull(request.getTxfee(), BigDecimal.ZERO));
    transferOut.setFromAccount(StringUtils.EMPTY);
    transferOut.setFromCoinAddress(StringUtils.EMPTY);
    transferOut.setToCoinAddress(request.getAddress());
    transferOut.setInnerOrderNo(request.getTxid());
    transferOut.setWithdrawStatus(WithdrawStatus.NEW);
    transferOut.setTxId(StringUtils.EMPTY);
    transferOut.setFailMessage(StringUtils.EMPTY);

    TransferOutService transferOutService = SpringContextHelper.getBean(TransferOutService.class);
    transferOutService.save(assetCode, transferOut);
    return transferOutService.queryById(assetCode, transferOut.getId());
  }
}
