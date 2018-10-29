package com.ourdax.coindocker.utils;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author think on 23/1/2018
 */
@Component
public class TransactionHelper {

  @Transactional
  public void doInCurrentTransaction(Runnable runnable) {
    runnable.run();
  }

  @Transactional
  public <T> T doInCurrentTransaction(Callable<T> callable) throws Exception {
    return callable.call();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void doInNewTransaction(Runnable runnable) {
    runnable.run();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> T doInNewTransaction(Callable<T> callable) throws Exception {
    return callable.call();
  }
}
