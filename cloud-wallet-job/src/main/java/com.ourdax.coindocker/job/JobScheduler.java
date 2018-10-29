package com.ourdax.coindocker.job;

import com.google.common.base.Predicates;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ourdax.coindocker.AssetCodeAware;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author think on 3/2/2018
 */
@Slf4j
public abstract class JobScheduler implements AssetCodeAware {

  private static final UncaughtExceptionHandler loggerHandler = (t, e) -> log.error("Uncaught exception: ", e);

  @Autowired
  private ApplicationContext applicationContext;

  private static final ThreadFactory threadFactory = new ThreadFactoryBuilder()
      .setNameFormat("asset-job-schedule-%d")
      .setUncaughtExceptionHandler(loggerHandler)
      .build();

  private Map<String, JobConfig> jobConfigs;


  // (job name -> job context)
  protected abstract Map<String, JobConfig> getJobConfigs();

  public final void scheduleJobs() {
    jobConfigs = getJobConfigs();

    Map<String, Job> jobs = applicationContext.getBeansOfType(Job.class);
    jobs.values().forEach((job) -> {
      JobConfig config = jobConfigs.get(job.getName());
      if (config == null) {
        throw new RuntimeException("JobContext not found");
      }
      try {
        log.info("Start scheduling {} job for {}, delay: {}, rate: {}",
            job, config.getAssetCode(), format(config.getDelay(), config.getTimeUnit()),
            format(config.getRate(), config.getTimeUnit()));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        config.setScheduledExecutor(executor);
        executor.scheduleAtFixedRate(
            () -> job.run(config),
            config.getDelay(),
            config.getRate(),
            config.getTimeUnit());
      } catch (Exception e) {
        log.error("Executing {} job error", job, e);
      }
    });
  }

  public void shutdown() {
    jobConfigs.values().stream()
        .map(JobConfig::getScheduledExecutor)
        .filter(Predicates.notNull())
        .forEach(this::shutdown);
  }

  private void shutdown(ScheduledExecutorService executor) {
    if (executor.isShutdown()) {
      return;
    }
    executor.shutdown();
    try {
      executor.awaitTermination(1, TimeUnit.SECONDS);
      executor.shutdownNow();
    } catch (InterruptedException e) {
      log.error("Shutdown ScheduledExecutorService unexpected interrupted", e);
    }
  }

  public String format(long time, TimeUnit unit) {
    return TimeUnit.SECONDS.convert(time, unit) + "s";
  }

}
