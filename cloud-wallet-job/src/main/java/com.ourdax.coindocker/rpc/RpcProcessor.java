package com.ourdax.coindocker.rpc;


import com.ourdax.coindocker.AssetCodeAware;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.enums.AssetCode;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author think on 11/1/2018
 */
public interface RpcProcessor extends AssetCodeAware {

  /**
   * 获取最近的区块号
   */
  Block getLatestBlock();

  /**
   * 查询区块的所有交易
   */
  BlockTrans queryTrans(Block block);


  /**
   * 查询从某个区块开始（不包括）的所有交易
   */
  BlockTrans queryTransSince(Block block);

  /**
  /**
   * 根据txId查询交易详情
   */
  List<TransInfo> queryTransInfo(String txId);


  /**
   * 查询账户中的余额
   */
  BigDecimal queryBalance();


  /**
   * 查询指定账户中的余额
   */
  BigDecimal queryBalance(String account);

  /**
   * 获取合约币所属链主流币种的余额
   */
  BigDecimal queryChainMajorTokenBalance();


  /**
   * 计算确认次数
   */
  int getConfirmationNum(Block block);

  /**
   * 获取交易状态
   */
  TransStatus getTransStatus(TransInfo transInfo);

  /**
   * 转账前处理，校验余额，解锁账户等
   */
  void preTransfer(RpcTransRequest rpcTransRequest);

  /**
   * 转账,从统一转出地址转
   */
  RpcTransResponse defaultTransfer(RpcTransRequest request);

  /**
   * 批量转账前处理，校验余额，解锁账户等
   * @param request
   */
  void preBatchTransfer(RpcBatchTransferRequest request);

  /**
   * 进行批量转账
   */
  RpcBatchTransferResponse batchTransfer(RpcBatchTransferRequest request);

  /**
   * 获取指定币种的统一转出地址（btc获取地址，而非账户）
   * @return
   */
  String getUniformAccount();

  /**
   * 指定地址或者账户转账
   **/
  RpcTransResponse transferFrom(String from, String to, BigDecimal amount);

  /**
   * 查询指定地址的合约币余额
   * */
  BigDecimal queryChainMajorTokenBalance(String inAddress);
}
