package com.ourdax.coindocker.common.base;

/**
 * @author think on 13/1/2018
 */
@FunctionalInterface
public interface RpcCall<R> {
  R call() throws Exception;
}
