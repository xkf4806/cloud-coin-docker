package com.ourdax.coindocker.domain;

import java.util.Date;
import lombok.Data;

/**
 * @author think on 12/1/2018
 */
@Data
public class MQProduceLog {
  private Long id;
  private String exchangeName;
  private String routerKey;
  private String message;
  private SendStatus status;
  private Date createTime;
  private Date updateTime;

  public enum SendStatus {
    NEW, SUCCESS
  }
}
