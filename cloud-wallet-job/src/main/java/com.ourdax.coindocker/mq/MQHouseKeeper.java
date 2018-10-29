package com.ourdax.coindocker.mq;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.common.utils.DateFormatUtil;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.common.utils.JsonUtils;
import com.ourdax.coindocker.dao.MQProduceLogDao;
import com.ourdax.coindocker.domain.MQProduceLog;
import com.ourdax.coindocker.domain.MQProduceLog.SendStatus;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author think on 12/1/2018
 */
@Component
@Slf4j
@EnableScheduling
public class MQHouseKeeper {
  @Autowired
  private MQProduceLogDao mqProduceLogDao;

  @Autowired
  private MQProducer mqProducer;

  private static final int LOG_SAVE_DAYS = 3;

  private static final int RESEND_MESSAGE_PERIOD = 1;


  @Scheduled(fixedRate = 60_000) // every 1 min.
  public void clearOldMessages() {
    Date to = DateUtil.minusDays(DateUtil.now(), LOG_SAVE_DAYS);
    log.info("Clearing old messages to {}", DateFormatUtil.formatyyyyMMddHHmmss(to));
    mqProduceLogDao.deleteSuccessByCreateTime(to);
  }

  @Scheduled(fixedRate = 10_000) // every 10 sec.
  public void resend() {
    Date to = DateUtil.now();
    Date from = DateUtil.minusDays(to, RESEND_MESSAGE_PERIOD);
    List<MQProduceLog> mqProduceLogs = mqProduceLogDao
        .selectByCreateTimeAndStatus(from, to, SendStatus.NEW);
    List<MQProduceLog> sent = Lists.newArrayListWithExpectedSize(mqProduceLogs.size());
    log.info("Resending message, time: {}, {} messages to resend.",
        DateFormatUtil.formatyyyyMMddHHmmss(DateUtil.now()), mqProduceLogs.size());

    if (CollectionUtils.isEmpty(mqProduceLogs)) {
      return;
    }
    mqProduceLogs.forEach(mqProduceLog -> {
      Message message = restoreMessage(mqProduceLog);
      try {
        mqProduceLog.setStatus(SendStatus.SUCCESS);
        mqProduceLogDao.updateStatus(mqProduceLog);
        mqProducer.sendMessage0(message);
        sent.add(mqProduceLog);
      } catch (Exception e) {
        log.error("Resend message error, message: {}", message, e);
      }
    });
//    mqProduceLogDao.batchUpdateStatus(sent);
  }

  private Message restoreMessage(MQProduceLog log) {
    Message message = new Message();
    message.setExchange(log.getExchangeName());
    message.setRouterKey(log.getRouterKey());
    message.setData(JsonUtils.parse(log.getMessage()));
    return message;
  }
}
