package com.ourdax.coindocker.job;

import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author think on 3/2/2018
 */
public class JobConfig {

  private AssetCode assetCode;

  private long delay;

  private long rate;

  private TimeUnit timeUnit;

  private ScheduledExecutorService scheduledExecutor;

  public JobConfig(AssetCode assetCode, long delay, long rate, TimeUnit timeUnit) {
    this.assetCode = assetCode;
    this.delay = delay;
    this.rate = rate;
    this.timeUnit = timeUnit;
  }

  public JobConfig() {
  }


  public ScheduledExecutorService getScheduledExecutor() {
    return scheduledExecutor;
  }

  public void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
    this.scheduledExecutor = scheduledExecutor;
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public long getRate() {
    return rate;
  }

  public void setRate(long rate) {
    this.rate = rate;
  }

  public AssetCode getAssetCode() {
    return assetCode;
  }

  public void setAssetCode(AssetCode assetCode) {
    this.assetCode = assetCode;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

}
