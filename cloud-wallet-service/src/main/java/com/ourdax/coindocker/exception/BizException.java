package com.ourdax.coindocker.exception;

/**
 * @author think on 24/1/2018
 */
public class BizException extends Exception {

  public BizException() {
    super();
  }

  public BizException(String message) {
    super(message);
  }

  public BizException(String message, Throwable cause) {
    super(message, cause);
  }

  public BizException(Throwable cause) {
    super(cause);
  }

  protected BizException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
