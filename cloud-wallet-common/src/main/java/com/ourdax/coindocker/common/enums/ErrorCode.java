package com.ourdax.coindocker.common.enums;

/**
 * 错误码
 * Created by wenzhiwei on 16/12/20.
 */
public enum ErrorCode {

    SUCCESS(0,"success"),
    DB_ERROR(501, "数据库错误"),
    ERROR(500,"系统内部错误!(system error!)");


    private int code;
    private String msg;

    ErrorCode(int code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
