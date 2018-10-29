package com.ourdax.coindocker.common.base;

/**
 * @author think on 13/1/2018
 */
public class RpcCallTemplate<R> {
  private final RpcCall<R> call ;

  public RpcCallTemplate(RpcCall<R> call) {
    this.call = call;
  }

  public R execute() {
    try {
      return call.call();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RpcException("rpc call error", e);
    }
  }
}