package com.ourdax.coindocker.controller;

import com.ourdax.coindocker.base.ApiCall;
import com.ourdax.coindocker.base.BaseController;
import com.ourdax.coindocker.base.Result;
import com.ourdax.coindocker.common.exception.ApiCallException;
import com.ourdax.coindocker.common.reqs.AssignAddrReq;
import com.ourdax.coindocker.common.resps.AssignAddrResp;
import com.ourdax.coindocker.service.AddressAssignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangjinyang on 2018/6/12.
 */
@RestController
@RequestMapping("/pool")
@Api(value = "地址池", description = "地址池", tags = "交易所接口-地址")
public class AddressAssignCtrl extends BaseController {

  @Autowired
  private AddressAssignService addressAssignService;

  @ApiOperation(value = "地址分配[DONE]")
  @RequestMapping(method = RequestMethod.POST, value = "/assign")
  Result<AssignAddrResp> assignAddress(@RequestBody AssignAddrReq req) {
    return new ApiCall<AssignAddrResp>() {
      @Override
      protected AssignAddrResp process() throws ApiCallException {
        return addressAssignService.assign(req);
      }
    }.execute();
  }

}
