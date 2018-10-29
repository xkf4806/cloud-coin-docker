package com.ourdax.coindocker.domain;

import java.util.Date;
import lombok.Data;

/**
 * @author think on 9/1/2018
 */
@Data
public class BlockCheck {
  private Integer id;
  private String assetCode;
  private String blockhash;
  private String blockheight;
  private Date updateDate;
}
