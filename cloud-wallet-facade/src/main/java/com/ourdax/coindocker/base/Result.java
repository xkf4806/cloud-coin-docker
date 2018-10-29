package com.ourdax.coindocker.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 接口返回统一定义
 *
 * @param <T>
 */
@ApiModel(value = "请求返回实体",description = "请求返回实体")
public class Result<T> {

    /**
     *
     */
    private static final long serialVersionUID = -2120979771572587887L;

    /**
     * 对外返回的对象
     */
    @ApiModelProperty(value = "返回数据字段",notes = "返回数据字段")
    private T data;

    /**
     * 返回状态码
     */
    @ApiModelProperty(value = "返回码(当且仅当0成功)",notes = "返回码")
    private int code;

    /**
     * 返回消息
     */
    @ApiModelProperty(value = "返回信息(成功为success)",notes = "返回信息")
    private String msg;

    public Result() {

        super();

    }

    public Result(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public Result(T data, int code, String msg) {
        super();
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    /**
     * 服务器unix utc时间戳秒值
     */
    public long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

}
