package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.TransferIn;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author think on 9/1/2018
 */
public interface TransferInDao {
  void insert(@Param("table") String table, @Param("transferIn") TransferIn transferIn);

  int updateTransferStatus(@Param("table") String table, @Param("transferIn") TransferIn transferIn);

  List<TransferIn> selectPendings(@Param("table") String tableName, @Param("from") Date from);

  int selectCountByTxId(@Param("table") String table, @Param("txId") String txId,
      @Param("to") String to, @Param("vout") String vout);

  List<TransferIn> selectLastestTxOfSwt(@Param("to") String to);

  TransferIn querySWTTransByTxId(@Param("txId") String txId);
}
