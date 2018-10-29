package com.ourdax.coindocker.job;

/**
 * @author think on 3/2/2018
 */
public interface Job {
  void run(JobConfig config);

  String getName();
}
