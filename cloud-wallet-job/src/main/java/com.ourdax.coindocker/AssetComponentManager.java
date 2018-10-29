package com.ourdax.coindocker;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.mq.TransferOutMessageListener;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.trans.TransHandler;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author think on 12/1/2018
 */
@Component(value = "assetComponentManager")
@Slf4j
public class AssetComponentManager {

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  @Autowired
  private ServerContext serverContext;

  private final EnumMap<AssetCode, RpcProcessor> rpcProcessors = new EnumMap<>(AssetCode.class);
  private final EnumMap<AssetCode, TransHandler> transHandlers = new EnumMap<>(AssetCode.class);

  private static final String RPC_PROCESSOR_POSTFIX = "RpcProcessor";
  private static final String TRANS_HANDLER_POSTFIX = "TransHandler";

  @PostConstruct
  public void init() {
    registerMessageListeners();
    registerTransHandlers();
    registerRpcProcessors();
  }


  private void registerMessageListeners() {
    getSupportedAssets().forEach(this::registerTransferOutMessageListenerFor);
  }

  private void registerTransferOutMessageListenerFor(AssetCode assetCode) {
    DefaultListableBeanFactory beanFactory =
        (DefaultListableBeanFactory) applicationContext.getBeanFactory();
    BeanDefinition beanDefinition = BeanDefinitionBuilder
        .genericBeanDefinition(TransferOutMessageListener.class)
        .addPropertyValue("assetCode", assetCode)
        .getBeanDefinition();
    beanFactory.registerBeanDefinition(getTransferOutMessageListenerName(assetCode), beanDefinition);
  }

  private String getTransferOutMessageListenerName(AssetCode assetCode) {
    return assetCode + TransferOutMessageListener.class.getSimpleName();
  }

  public List<TransferOutMessageListener> getTransferOutMessageListeners() {
    return getSupportedAssets().stream()
        .map(this::getTransferOutMessageListener)
        .collect(Collectors.toList());
  }

  private TransferOutMessageListener getTransferOutMessageListener(AssetCode assetCode) {
    return ((TransferOutMessageListener) applicationContext.getBean(
        getTransferOutMessageListenerName(assetCode)));
  }

  private void registerTransHandlers() {
    getSupportedAssets().forEach(assetCode -> {
      String beanName = assetCode + TRANS_HANDLER_POSTFIX;
      TransHandler transHandler = (TransHandler) applicationContext.getBean(beanName);
      transHandlers.put(assetCode, transHandler);
      log.info("TransHandler for {} registered, name: {}", assetCode, transHandler);
    });
  }

  public TransHandler getTransHandler(AssetCode assetCode) {
    return transHandlers.get(assetCode);
  }

  private void registerRpcProcessors() {
    getSupportedAssets().forEach(assetCode -> {
          String beanName = assetCode + RPC_PROCESSOR_POSTFIX;
          RpcProcessor rpcProcessor = (RpcProcessor) applicationContext.getBean(beanName);
          rpcProcessors.put(assetCode, rpcProcessor);
          log.info("RpcProcessor for {} registered, name: {}", assetCode, rpcProcessor);
        }
    );
  }

  public RpcProcessor getRpcProcessor(AssetCode assetCode) {
    return rpcProcessors.get(assetCode);
  }

  private EnumSet<AssetCode> getSupportedAssets() {
    return serverContext.getSupportedAssets();
  }
}
