package com.ourdax.coindocker.utils;


import com.ourdax.coindocker.base.Result;
import com.ourdax.coindocker.common.enums.ErrorCode;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 返回结果封装
 * Created by wenzhiwei on 16/12/20.
 */
public class ResponseUtil {

    private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);


    public static Result ok(Object data) {

        Result result = new Result();

        result.setData(data);
        result.setMsg(ErrorCode.SUCCESS.getMsg());
        result.setCode(ErrorCode.SUCCESS.getCode());

        return result;

    }

    public static Result fail() {

        Result result = new Result();

        result.setMsg(ErrorCode.ERROR.getMsg());
        result.setCode(ErrorCode.ERROR.getCode());

        return result;

    }


    public static Result fail(ErrorCode errorCode) {

        Result result = new Result();

        result.setMsg(errorCode.getMsg());
        result.setCode(errorCode.getCode());

        return result;

    }


    /**
     * 回写信息
     *
     * @param response
     * @param charset
     * @param msg
     * @throws IOException
     */
    public static void writeMsgToPage(HttpServletResponse response,
                                      String charset, String msg) {

        try {

            response.setContentType("text/html;charset=" + charset);
            response.getWriter().write(msg);
            response.getWriter().flush();

        } catch (Exception e) {

            logger.error("writeError error!", e);

        }

    }

}
