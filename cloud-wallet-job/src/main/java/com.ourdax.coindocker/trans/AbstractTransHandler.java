package com.ourdax.coindocker.trans;

import com.ourdax.coindocker.domain.TransferOut;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author think on 14/1/2018
 */
@Slf4j
public abstract class AbstractTransHandler implements TransHandler {

  @Override
  public void transfer(List<TransferOut> transferOuts) {
    transferOuts.forEach(this::transfer);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
