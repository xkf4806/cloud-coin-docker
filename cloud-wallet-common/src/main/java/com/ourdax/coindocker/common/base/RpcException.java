package com.ourdax.coindocker.common.base;

/**
 * @author think on 13/1/2018
 */
public class RpcException extends AssetException {

  public RpcException() {
  }

  public RpcException(String message) {
    super(message);
  }

  public RpcException(String message, Throwable cause) {
    super(message, cause);
  }
}
