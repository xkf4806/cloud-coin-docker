package com.ourdax.coindocker.common.reqs;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 分配地址请求参数
 * Created by zhangjinyang on 2018/6/12.
 */
@Data
@ApiModel(description = "分配地址请求参数")
public class AssignAddrReq {

  private String snapshotId;

  private String asset;

}
