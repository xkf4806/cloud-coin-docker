package com.ourdax.coindocker.base;

import com.ourdax.coindocker.common.exception.ApiCallException;
import com.ourdax.coindocker.utils.ResponseUtil;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangjinyang on 16/12/20.
 */
public abstract class ApiCall<T> {

    protected Logger logger = LoggerFactory.getLogger(ApiCall.class);

    protected abstract T process() throws ApiCallException;

    public Result<T> execute()  {
        Result res = new Result();
        try {
            T result = process();
            res =  ResponseUtil.ok(result);
        } catch (ApiCallException e) {
            logger.error("api call Exception", e.getMessage());
            res =  ResponseUtil.fail(e.getError());
        }  catch (Exception e) {
            logger.error("unexpected exception ", e);
            res =  ResponseUtil.fail();
        }
        logger.info("ApiCall result:{}",res);
        return res;
    }


}
