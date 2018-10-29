package com.ourdax.coindocker.dao;

import com.ourdax.coindocker.domain.MQProduceLog;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author think on 12/1/2018
 */
public interface MQProduceLogDao {

  void insert(MQProduceLog log);

  void batchInsert(@Param("list") List<MQProduceLog> logs);

  List<MQProduceLog> selectByCreateTimeAndStatus(@Param("start") Date start, @Param("end") Date end,
      @Param("status") MQProduceLog.SendStatus status);

  int deleteSuccessByCreateTime(@Param("end") Date end);


  int updateStatus(MQProduceLog log);


  int batchUpdateStatus(@Param("list") List<MQProduceLog> logs);
}
