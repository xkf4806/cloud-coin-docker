package com.ourdax.coindocker.mq;

import com.ourdax.coindocker.AssetCodeAware;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.amqp.core.MessageListener;

/**
 * @author think on 10/1/2018
 */
public interface AssetMessageListener<T> extends MessageListener, AssetCodeAware {
  AssetCode getAssetCode();

  Class<T> getMessageType();
}
