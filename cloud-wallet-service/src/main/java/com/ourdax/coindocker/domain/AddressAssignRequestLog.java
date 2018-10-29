package com.ourdax.coindocker.domain;

import com.ourdax.coindocker.common.enums.AssignStatus;
import java.util.Date;
import lombok.Data;

@Data
public class AddressAssignRequestLog {

  private Integer id;

  private String asset;

  private String snapshotId;

  private AssignStatus status;

  private String assignedAddr;

  private Date createTime;

  private Date updateTime;


}