package com.ourdax.coindocker.mq;

import com.google.common.base.Optional;
import com.ourdax.coindocker.common.utils.JsonUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

/**
 * @author think on 10/1/2018
 */
@Slf4j
public abstract class AbstractAssetMessageListener<T> implements AssetMessageListener<T> {

  @Override
  public final void onMessage(Message message) {
    MessageProperties messageProperties = message.getMessageProperties();

    /**获取消息队列名称，得到消息针对的真实币种*/
    String queue = messageProperties.getConsumerQueue();
    String[] assets = queue.split("\\.");
    String assetCode = assets[2];

    String charset = Optional.fromNullable(
        (String)messageProperties.getHeaders().get("charset")).or(StandardCharsets.UTF_8.name());
    String jsonStr;
    try {
      jsonStr = new String(message.getBody(), charset);
    } catch (UnsupportedEncodingException e3) {
      log.error("Decode message failed, charset: {}, message: {}", charset, message);
      return;
    }

    T data;
    try {
      data = JsonUtils.parse(jsonStr, getMessageType());
    } catch (Exception e) {
      log.error("Incorrect message json format, message: {}", jsonStr, e);
      return;
    }

    try {
      log.info("Receiving message: {}", data);
      processMessage(data, assetCode);
    } catch (Exception e) {
      log.error("Process message error, message: {}", data, e);
    }
  }

  protected abstract void processMessage(T message, String assetCode);
}
