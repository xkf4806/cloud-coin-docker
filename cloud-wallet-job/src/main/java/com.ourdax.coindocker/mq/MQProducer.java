package com.ourdax.coindocker.mq;

import com.ourdax.coindocker.common.utils.JsonUtils;
import com.ourdax.coindocker.dao.MQProduceLogDao;
import com.ourdax.coindocker.domain.MQProduceLog;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author think on 10/1/2018
 */
@Component
@Slf4j
public class MQProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private MQProduceLogDao mqProduceLogDao;


  /**
   * 同步发送消息
   * @param message 消息体
   */
  public void sendMessage(Message message) {
    sendMessage(message, true);
  }

  void sendMessage0(Message message) {
    log.info("Sending message, routeKey:  " + message.getRouterKey() + " data: " + message.getData());
    rabbitTemplate.convertAndSend(message.getExchange(), message.getRouterKey(), message.getData());
  }

  /**
   * 发送消息
   * @param message 消息体
   * @param sync 是否同步发送，若是则直接发送，反之异步发送
   */
  public void sendMessage(Message message, boolean sync) {
    if (sync) {
      trySend(message);
    } else {
      saveMessage(message);
    }
  }

  private void trySend(Message message) {
    try {
      sendMessage0(message);
      return;
    } catch (Exception e) {
      log.error("Send message error", e);
    }
    saveMessage(message);
  }

  private void saveMessage(Message message) {
    mqProduceLogDao.insert(buildMessageLog(message));
  }

  /**
   * 批量发送消息
   * @param messageList 消息列表
   * @param sync 是否同步发送
   */
  public void sendMessages(List<Message> messageList, boolean sync) {
    if (CollectionUtils.isEmpty(messageList)) {
      return;
    }

    if (sync) {
      messageList.forEach(this::trySend);
    } else {
      saveMessages(messageList);
    }
  }

  private void saveMessages(List<Message> messages) {
    List<MQProduceLog> logs = messages.stream()
        .map(this::buildMessageLog)
        .collect(Collectors.toList());
    mqProduceLogDao.batchInsert(logs);
  }

  private MQProduceLog buildMessageLog(Message message) {
    MQProduceLog log = new MQProduceLog();
    log.setExchangeName(message.getExchange());
    log.setRouterKey(message.getRouterKey());
    log.setMessage(JsonUtils.toString(message.getData()));
    return log;
  }

}
