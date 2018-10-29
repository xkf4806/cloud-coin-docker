package com.ourdax.coindocker.service.impl;

import com.ourdax.coindocker.common.enums.AssignStatus;
import com.ourdax.coindocker.common.exception.ApiCallException;
import com.ourdax.coindocker.common.reqs.AssignAddrReq;
import com.ourdax.coindocker.common.resps.AssignAddrResp;
import com.ourdax.coindocker.dao.AddressAssignRequestDao;
import com.ourdax.coindocker.dao.AddressPoolDao;
import com.ourdax.coindocker.domain.AddressAssignRequestLog;
import com.ourdax.coindocker.domain.AddressPool;
import com.ourdax.coindocker.domain.AddressPool.AddressStatus;
import com.ourdax.coindocker.service.AddressAssignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhangjinyang on 2018/6/12.
 */
@Slf4j
@Service
public class AddressAssignServiceimpl implements AddressAssignService {

  private static final String TABLE_PREFIX = "address_pool_";

  @Autowired
  private AddressPoolDao addressPoolDao;

  @Autowired
  private AddressAssignRequestDao addressAssignRequestDao;

  @Override
  @Transactional
  public AssignAddrResp assign(AssignAddrReq req) throws ApiCallException {

    log.info("为 {} 分配新地址,snapshotId={}",req.getAsset(), req.getSnapshotId());
    AddressAssignRequestLog addressAssignRequestLog = addressAssignRequestDao
        .selectBySnapshotId(req.getSnapshotId());
    if (addressAssignRequestLog != null) {
      if (AssignStatus.SUCCESS.equals(addressAssignRequestLog.getStatus())) {
        return new AssignAddrResp(addressAssignRequestLog.getSnapshotId(),
            addressAssignRequestLog.getAsset(), addressAssignRequestLog.getAssignedAddr());
      } else {
        String assignAddress = assignAddress(req.getAsset());
        setAddressAssignRequestToSuccess(addressAssignRequestLog, assignAddress);
        return new AssignAddrResp(addressAssignRequestLog.getSnapshotId(),
            addressAssignRequestLog.getAsset(), assignAddress);
      }
    } else {
      AddressAssignRequestLog addressAssignRequestLogNew = new AddressAssignRequestLog();
      addressAssignRequestLogNew.setStatus(AssignStatus.NEW);
      addressAssignRequestLogNew.setAsset(req.getAsset());
      addressAssignRequestLogNew.setSnapshotId(req.getSnapshotId());
      addressAssignRequestDao.insert(addressAssignRequestLogNew);
      String addr = assignAddress(req.getAsset());
      setAddressAssignRequestToSuccess(addressAssignRequestLogNew, addr);
      return new AssignAddrResp(addressAssignRequestLogNew.getSnapshotId(),
          addressAssignRequestLogNew.getAsset(), addr);
    }

  }

  private String assignAddress(String asset) {

    AddressPool addressPool = addressPoolDao
        .selectOneUnusedAddress(TABLE_PREFIX + asset.toLowerCase());
    addressPool.setAddressStatus(AddressStatus.USED);
    addressPoolDao.updateById(TABLE_PREFIX + asset.toLowerCase(), addressPool);

    return addressPool.getCoinAddress();

  }

  private void setAddressAssignRequestToSuccess(AddressAssignRequestLog addressAssignRequestLog, String assignAddress){
    addressAssignRequestLog.setAssignedAddr(assignAddress);
    addressAssignRequestLog.setStatus(AssignStatus.SUCCESS);
    addressAssignRequestDao.updateBySnapshotId(addressAssignRequestLog);
  }
}
