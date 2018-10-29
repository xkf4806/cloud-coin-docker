package com.ourdax.coindocker.common.base;

/**
 * @author think on 23/1/2018
 */
public class AssetException extends RuntimeException {

  public AssetException() {
    super();
  }

  public AssetException(String message) {
    super(message);
  }

  public AssetException(String message, Throwable cause) {
    super(message, cause);
  }

  public AssetException(Throwable cause) {
    super(cause);
  }
}
