package com.ourdax.coindocker.common.exception;

import com.ourdax.coindocker.common.enums.ErrorCode;

/**
 * created by zhangjinyang 06/12/2018
 */
public class ApiCallException extends Exception {

    private static final long serialVersionUID = -787360420101877795L;

    protected ErrorCode error;

    public ApiCallException() {
        super();
    }

    public ApiCallException(ErrorCode error, Throwable cause) {
        super(error.getMsg(), cause);
        this.error = error;
    }

    public ApiCallException(ErrorCode error) {
        super(error.getMsg());
        this.error = error;
    }

    public ApiCallException(String error) {
        super(error);
        this.error.setMsg(error);
    }

    public ErrorCode getError() {
        return error;
    }

    public int getErrorCode() {
        return error.getCode();
    }

    public String getErrorMsg() {
        return error.getMsg();
    }

}
