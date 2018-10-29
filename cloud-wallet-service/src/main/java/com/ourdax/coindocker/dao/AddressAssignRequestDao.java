package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.AddressAssignRequestLog;
import org.apache.ibatis.annotations.Param;

/**
 *create by zhangjinyang 06/12/2018
 */
public interface AddressAssignRequestDao {

  int insert(AddressAssignRequestLog requestLog);

  int updateBySnapshotId(AddressAssignRequestLog requestLog);

  AddressAssignRequestLog selectBySnapshotId(@Param("snapshotId") String snapshotId);

}
