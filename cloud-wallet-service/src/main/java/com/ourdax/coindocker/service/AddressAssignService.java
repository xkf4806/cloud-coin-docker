package com.ourdax.coindocker.service;

import com.ourdax.coindocker.common.exception.ApiCallException;
import com.ourdax.coindocker.common.reqs.AssignAddrReq;
import com.ourdax.coindocker.common.resps.AssignAddrResp;

/**
 * Created by zhangjinyang on 2018/6/12.
 */
public interface AddressAssignService {

  AssignAddrResp assign(AssignAddrReq req) throws ApiCallException;

}
