package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.TransferOut;
import com.ourdax.coindocker.service.TransferOutService;
import java.math.BigDecimal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 10/1/2018
 */
public class TransferOutServiceTest extends AbstractTestCase {

  @Autowired
  private TransferOutService transferOutService;


  @Test
  public void testSave() {
    TransferOut transferOut = new TransferOut();
    transferOut.setAssetCode(AssetCode.EOS.name());
    transferOut.setAmount(new BigDecimal("1.0"));
    transferOut.setTxFee(new BigDecimal("0.1"));
    transferOut.setFromAccount("from account");
    transferOut.setFromCoinAddress("from coin address");
    transferOut.setToCoinAddress("to coin address");
    transferOut.setInnerOrderNo("333");
    transferOut.setTxId("1233444");
    transferOut.setFailMessage("fail");
    transferOutService.save(AssetCode.EOS, transferOut);
  }
}
