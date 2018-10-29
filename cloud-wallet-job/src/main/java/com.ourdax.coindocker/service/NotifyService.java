package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.mq.messages.TransferResult;

/**
 * @author think on 14/1/2018
 */
public interface NotifyService {
   void sendTransferInNotification(AssetCode assetCode, TransferResult result);

   void sendTransferOutNotification(AssetCode assetCode, TransferResult result);
}
