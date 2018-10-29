package com.ourdax.coindocker.mq;

import lombok.Data;
import lombok.ToString;

/**
 * @author think on 12/1/2018
 */
@Data
@ToString
public class Message {
  private String exchange;
  private String routerKey;
  private Object data;
}
