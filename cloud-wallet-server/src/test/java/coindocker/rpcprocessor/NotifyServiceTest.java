package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.google.common.util.concurrent.Uninterruptibles;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.AssetStatus;
import com.ourdax.coindocker.service.NotifyService;
import com.ourdax.coindocker.mq.messages.TransferResult;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 11/1/2018
 */
public class NotifyServiceTest extends AbstractTestCase {

  @Autowired
  private NotifyService notifyService;





  @Test
  public void testTransferInNotification() {
    for (int i = 0; i < 10; i++) {
      TransferResult result = new TransferResult();
      result.setAmount(BigDecimal.ONE);
      result.setAssetCode(AssetCode.EOS.name());
      result.setAssetStatus(AssetStatus.CONFIRM.name());
      result.setFromWallet("from wallet");
      result.setOrderId("1111111111");
      result.setToWallet("11111111111111");
//    result.setTxId("xxxxxxxxxxxxxxxxxxxxx");:
      notifyService.sendTransferInNotification(AssetCode.EOS, result);

      Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
    }
  }

}
