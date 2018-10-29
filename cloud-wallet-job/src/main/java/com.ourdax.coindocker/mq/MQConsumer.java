package com.ourdax.coindocker.mq;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.common.enums.ACTContractEnum;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.QRC20Enum;
import com.ourdax.coindocker.utils.AssetMQKeyUtils;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author think on 10/1/2018
 */
@Component
@Slf4j
public class MQConsumer {

  @Autowired
  private DirectExchange directExchange;

  @Autowired
  private MQProperties mqProperties;

  @Autowired
  private ConnectionFactory connectionFactory;

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  @Autowired
  private RabbitAdmin rabbitAdmin;

  @Autowired
  private AssetComponentManager manager;

  @PostConstruct
  public void init() {
    registerQueuesAndListeners();
  }

  private void registerQueuesAndListeners() {
    registerTransferOutListeners();
    rabbitAdmin.initialize();
  }

  private void registerTransferOutListeners() {
     manager.getTransferOutMessageListeners().forEach(listener -> {
      AssetCode assetCode = listener.getAssetCode();
       /*** 添加对合约代币的适配 */
       if (AssetCode.QRC20.equals(assetCode)){
         Lists.newArrayList(QRC20Enum.values()).stream().forEach(asset -> {
           registerQueue(AssetCode.valueOf(asset.name()), listener);
         });
       } else if(AssetCode.ACTCONTRACT.equals(assetCode)){
         Lists.newArrayList(ACTContractEnum.values()).stream().forEach(asset -> {
           registerQueue(AssetCode.valueOf(asset.name()), listener);
         });
       } else {
         registerQueue(assetCode, listener);
       }
    });
  }

  private void registerQueue(AssetCode assetCode, TransferOutMessageListener listener){
    Queue queue = new Queue(getQueueName(assetCode), true, false, false);
    Binding binding = BindingBuilder.bind(queue).to(directExchange).with(getBindKey(assetCode));
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
    container.setQueues(queue);
    container.setExposeListenerChannel(true);
    container.setMaxConcurrentConsumers(1);
    container.setConcurrentConsumers(1);
    container.setAcknowledgeMode(AcknowledgeMode.AUTO); // 设置确认模式手工确认
    container.setMessageListener(listener);

    ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
    beanFactory.registerSingleton(assetCode + "TransferOutQueue", queue);
    beanFactory.registerSingleton(assetCode + "TransferOutQueueBinding", binding);
    beanFactory.registerSingleton(assetCode + "SimpleMessageListenerContainer", container);
    log.info("Registering mq elements for " + assetCode);
  }

  private String getBindKey(AssetCode assetCode) {
    return AssetMQKeyUtils.getKeyFor(assetCode, mqProperties.getTransferOutKey());
  }

  private String getQueueName(AssetCode assetCode) {
    return AssetMQKeyUtils.getKeyFor(assetCode, mqProperties.getTransferOutQueue());
  }

}
