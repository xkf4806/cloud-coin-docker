package com.ourdax.coindocker.job;

/**
 * @author think on 3/2/2018
 */
public abstract class AbstractJob implements Job {
  @Override
  public String toString() {
    return getName();
  }
}
