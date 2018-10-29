package com.ourdax.coindocker.asset.bitcoin;

import static java.util.Comparator.comparing;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.domain.TransferOut;
import com.ourdax.coindocker.trans.DefaultTransHandler;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author think on 9/2/2018
 */
public abstract class BTCBasedDefaultHandler extends DefaultTransHandler {

  @Override
  public void transfer(List<TransferOut> transferOuts) {
    List<TransferOut> sorted = transferOuts.stream()
        .sorted(comparing(TransferOut::getCreateDate))
        .collect(Collectors.toList());

    // partitioned by size
    List<List<TransferOut>> partitionedList = Lists.partition(sorted, getBatchSize());
    partitionedList.forEach(batch -> {
      if (batch.size() == getBatchSize()) {
        batchTransferIfDistinct(batch);
      } else {
        List<TransferOut> remainings = batch.stream()
            .filter(this::tooOld)
            .collect(Collectors.toList());

        if (remainings.size() > 1) {
          batchTransferIfDistinct(remainings);
        } else if (remainings.size() == 1) {
          transfer(remainings.get(0));
        }
      }
    });
  }

  private void batchTransferIfDistinct(List<TransferOut> batch) {
    long distinctAddressCount = batch.stream()
        .map(TransferOut::getToCoinAddress)
        .distinct()
        .count();
    if (distinctAddressCount == batch.size()) {
      batchTransfer(batch);
    } else {
      batch.forEach(this::transfer);  // have same toAddress, cannot transfer in batch.
    }
  }

  private boolean tooOld(TransferOut transferOut) {
    return DateUtil.minusMinutes(DateUtil.now(), getWaitTimeThresholdInMins())
        .after(transferOut.getCreateDate());
  }

  protected abstract Integer getBatchSize();

  protected abstract Integer getWaitTimeThresholdInMins();
}
