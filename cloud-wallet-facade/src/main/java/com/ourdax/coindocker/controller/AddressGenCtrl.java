package com.ourdax.coindocker.controller;

import com.ourdax.coindocker.base.ApiCall;
import com.ourdax.coindocker.base.BaseController;
import com.ourdax.coindocker.base.Result;
import com.ourdax.coindocker.common.exception.ApiCallException;
import com.ourdax.coindocker.common.reqs.AssignAddrReq;
import com.ourdax.coindocker.common.resps.AssignAddrResp;
import com.ourdax.coindocker.service.AddressAssignService;
import com.ourdax.coindocker.service.AddressGenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangjinyang on 2018/6/12.
 */
@RestController
@RequestMapping("/address")
@Api(value = "地址生成", description = "地址池", tags = "交易所接口-地址")
public class AddressGenCtrl extends BaseController {

  @Autowired
  private AddressGenService addressGenService;

  @ApiOperation(value = "生成地址[DONE]")
  @RequestMapping(method = RequestMethod.GET, value = "/gen")
  Result<Boolean> assignAddress(@RequestParam("asset") String asset,
      @RequestParam("number") String number,
      @RequestParam(value = "account", required = false) String account,
      @RequestParam(value = "passPhrase", required = false) String passPhrase) {
    return new ApiCall<Boolean>() {
      @Override
      protected Boolean process() throws ApiCallException {
        return addressGenService
            .generateAssetAddresses(asset, Integer.parseInt(number), account, passPhrase);
      }
    }.execute();
  }

}
