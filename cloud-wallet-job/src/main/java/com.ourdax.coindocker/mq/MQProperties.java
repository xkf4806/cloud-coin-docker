package com.ourdax.coindocker.mq;

import lombok.Data;

/**
 * @author think on 9/1/2018
 */
@Data
public class MQProperties {
  private String exchange;

  private String transferInKey;

  private String transferOutKey;

  private String returnKey;

  private String transferOutQueue;
}
