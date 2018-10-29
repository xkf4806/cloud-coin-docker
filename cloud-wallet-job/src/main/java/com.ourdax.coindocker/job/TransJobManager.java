package com.ourdax.coindocker.job;

import com.ourdax.coindocker.ServerContext;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author think on 14/1/2018
 */
@Component
@Slf4j
public class TransJobManager {

  @Autowired
  private ServerContext serverContext;

  @Autowired
  private ApplicationContext applicationContext;


  @PostConstruct
  public void runSchedulers() {
    Map<String, DefaultJobScheduler> schedulers = applicationContext.getBeansOfType(DefaultJobScheduler.class);
    schedulers.values().stream()
        .filter(scheduler -> serverContext.getSupportedAssets().contains(scheduler.getAssetCode()))
        .peek(scheduler -> log.info("Run job scheduler for {}", scheduler.getAssetCode()))
        .forEach(DefaultJobScheduler::scheduleJobs);
  }

  @PreDestroy
  public void shutdownSchedulers() {
    Map<String, JobScheduler> schedulers = applicationContext.getBeansOfType(JobScheduler.class);
    schedulers.values().stream()
        .filter(scheduler -> serverContext.getSupportedAssets().contains(scheduler.getAssetCode()))
        .forEach(JobScheduler::shutdown);

  }

}
