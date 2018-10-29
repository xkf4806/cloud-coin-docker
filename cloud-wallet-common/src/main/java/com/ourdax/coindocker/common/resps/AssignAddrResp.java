package com.ourdax.coindocker.common.resps;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zhangjinyang on 2018/6/12.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分配地址返回结果")
public class AssignAddrResp {

  private String snapshotId;

  private String asset;

  private String address;

}
