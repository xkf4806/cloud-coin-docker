package com.ourdax.coindocker.service.impl;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.mq.MQProducer;
import com.ourdax.coindocker.mq.MQProperties;
import com.ourdax.coindocker.mq.Message;
import com.ourdax.coindocker.mq.messages.TransferResult;
import com.ourdax.coindocker.service.NotifyService;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author think on 10/1/2018
 */
@Service(value = "notifyService")
@Slf4j
public class NotifyServiceImpl implements NotifyService {
  @Autowired
  private MQProperties mqProperties;

  @Autowired
  private MQProducer mqProducer;

  @Override
  public void sendTransferInNotification(AssetCode assetCode, TransferResult result) {
    log.info("Sending transfer in notification of {}, content: {}", assetCode, result);
    Message message = new Message();
    message.setExchange(mqProperties.getExchange());
    message.setRouterKey(getRouterKey(assetCode, mqProperties.getTransferInKey()));
    message.setData(result);
    mqProducer.sendMessage(message, false);
  }

  @Override
  public void sendTransferOutNotification(AssetCode assetCode, TransferResult result) {
    log.info("Sending transfer out notification of {}, content: {}", assetCode, result);
    Message message = new Message();
    message.setExchange(mqProperties.getExchange());
    message.setRouterKey(getRouterKey(assetCode, mqProperties.getReturnKey()));
    message.setData(result);
    mqProducer.sendMessage(message, false);
  }

  private String getRouterKey(AssetCode assetCode, String keyTemplate) {
    MessageFormat messageFormat = new MessageFormat(keyTemplate);
    return messageFormat.format(new Object[] {assetCode.name().toLowerCase()});
  }
}
