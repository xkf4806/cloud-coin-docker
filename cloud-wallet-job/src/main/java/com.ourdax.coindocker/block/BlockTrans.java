package com.ourdax.coindocker.block;

import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * @author think on 12/1/2018
 */
@Data
@ToString
public class BlockTrans {
  private Block block;
  private List<TransInfo> trans;
}
