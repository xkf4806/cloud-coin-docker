package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.service.TransferInService;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 10/1/2018
 */
public class TransferInServiceTest extends AbstractTestCase {

  @Autowired
  private TransferInService transferInService;


  @Test
  public void testSave() {
    TransferIn transferIn = new TransferIn();
    transferIn.setAssetCode("BTM");
    transferIn.setTxId("0xf14dc9f8e24e68856dc20c6ce4de3e49aadf7a73c7701ef401d322780635d419");
    transferIn.setBlockhash("0xf4e52f479dc3eaf0dbfd77c470a40268e6f31caef8fab2b3bcc4a09e422a6a8g");
    transferIn.setToCoinAddress("0x8011bbd4c3b7349c1eda8ec8f16824cfbd6675c2");
    transferIn.setFromCoinAddress("fromcoinaddress");
    transferIn.setCategory("receive");
    transferIn.setAmount(new BigDecimal("4.0"));
    transferIn.setConfirmNum(474);
    transferIn.setFailMessage("error");
    transferInService.save(AssetCode.EOS, transferIn);
  }

  @Test
  public void testUpdateStatus() {
    TransferIn transferIn = new TransferIn();
    transferIn.setId(45);
    transferIn.setConfirmNum(111);
    transferIn.setUpdateDate(new Date());
    transferIn.setBlockhash("xxxxxxxxxx");
    transferIn.setBlockNum("11111");
    transferIn.setDepositStatus(TransferIn.DepositStatus.FAIL);
    transferInService.updateTransferStatus(AssetCode.EOS, transferIn);
  }

}
