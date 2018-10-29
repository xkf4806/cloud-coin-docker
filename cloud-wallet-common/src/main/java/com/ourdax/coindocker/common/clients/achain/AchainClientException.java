package com.ourdax.coindocker.common.clients.achain;

import com.ourdax.coindocker.common.clients.achain.common.ErrorCode;

/**
 * @author think on 30/1/2018
 */
public class AchainClientException extends Exception {

  private ErrorCode errorCode;

  public AchainClientException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public AchainClientException(ErrorCode errorCode, String message)  {
    super(message);
    this.errorCode = errorCode;
  }

  public AchainClientException(ErrorCode errorCode, Throwable cause)  {
    super(cause);
    this.errorCode = errorCode;
  }

  public AchainClientException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
