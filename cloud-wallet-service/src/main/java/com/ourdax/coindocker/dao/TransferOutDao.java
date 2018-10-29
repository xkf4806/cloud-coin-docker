package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.TransferOut;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author think on 9/1/2018
 */
public interface TransferOutDao {
  void insert(@Param("table") String table, @Param("transferOut") TransferOut transferOut);

  int updateWithdrawStatusById(@Param("table") String table,
      @Param("transferOut") TransferOut transferOut);

  List<TransferOut> selectPendings(@Param("table") String tableName, @Param("from") Date date);

  List<TransferOut> selectUnsents(@Param("table") String tableName, @Param("from") Date date);

  TransferOut selectById(@Param("table") String tableName, @Param("id") Integer id);

  int updateTxId(@Param("table") String tableName, @Param("transferOut") TransferOut transferOut);
}
