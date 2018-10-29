package coindocker.service;

import coindocker.AbstractTestCase;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.dao.OfflineAddressDao;
import com.ourdax.coindocker.domain.OfflineAddress;
import com.ourdax.coindocker.domain.OfflineAddress.OfflineAddrStatus;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.domain.TransferIn.DepositStatus;
import com.ourdax.coindocker.service.TransferInService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/8/3.
 */

public class TransferInServiceTest extends AbstractTestCase{

  @Autowired
  private TransferInService transferInService;

  @Autowired
  private OfflineAddressDao offlineAddressDao;

  @Test
  public void createTobeCollectedRecord(){

    List<TransferIn> transferIns = transferInService.queryPendings(AssetCode.OMG);
    TransferIn transferIn = transferIns.get(0);
    transferIn.setDepositStatus(DepositStatus.CONFIRMED);
    transferIn.setConfirmNum(32);
    transferIn.setBlockNum("6067611");
    transferIn.setBlockhash("0x71a0488cf7e3d9103ca144f1165b8da3183d4445430117cbe76f9ffecd0fdc09");
    transferIn.setUpdateDate(DateUtil.now());
    transferIn.setFee(new BigDecimal("0.0001"));

    transferInService.updateTransferStatus(AssetCode.OMG, transferIn);
  }

  @Test
  public void offlineAddressDaoTest(){
//    List<OfflineAddress> offlineAddresses = offlineAddressDao
//        .queryAddressByStatus(AssetCode.OMG.name(), Lists.newArrayList(
//            OfflineAddrStatus.INUSE, OfflineAddrStatus.UNASSIGN));
//
//    OfflineAddress offlineAddress = offlineAddresses.get(1);
//    offlineAddress.setAmount(new BigDecimal(50));
//    offlineAddress.setStatus(OfflineAddrStatus.QUIT);
//
//    OfflineAddress offlineAddress1 = offlineAddresses.get(0);
//    offlineAddress1.setAmount(new BigDecimal(2));
//    offlineAddress1.setStatus(OfflineAddrStatus.INUSE);
//    offlineAddressDao.updateBatch(Lists.newArrayList(offlineAddress, offlineAddress1));
    OfflineAddress offlineAddress = offlineAddressDao
        .queryByAddressAndAsset("0x30425d5c4b2e5dfe05273c9cdcdb8f3aa74b7dc5", "OMG");
    System.out.println(offlineAddress.getAddress() + "-----" + offlineAddress.getAmount());
  }

}
