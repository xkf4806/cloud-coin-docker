package com.ourdax.coindocker.service;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.ourdax.coindocker.clients.AssetClient;
import com.ourdax.coindocker.clients.ClientManager;
import com.ourdax.coindocker.common.clients.achain.AchainClient;
import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.exception.ApiCallException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.admin.Admin;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Service
@Slf4j
public class AddressGenServiceImpl implements AddressGenService {

  @Autowired
  private ClientManager manager;

  @Override
  public Boolean generateAssetAddresses(String asset, Integer number, String account,
      String passPhrase)
      throws ApiCallException {
    log.info("为{}生成{}个新地址，开始……", asset, number);
    //act 不需要调用客户端来生成子地址
    if (AssetCode.ACT.equals(AssetCode.valueOf(asset)) ||
        AssetCode.ACTCONTRACT.equals(AssetCode.valueOf(asset))) {
      generateAddresses(asset, null, number, account, passPhrase);
    }

    AssetClient assetClient = manager.getSpecificInstance(asset);
    Object clientInstance = assetClient.getClientInstance();
    generateAddresses(asset, clientInstance, number, account, passPhrase);
    log.info("为{}生成{}个新地址，结束……", asset, number);
    return true;
  }

  private void generateAddresses(String asset, Object clientInstance, Integer number,
      String account, String passPhrase)
      throws ApiCallException {
    if (clientInstance instanceof BtcdClient) {
      BtcdClient btcdClient = (BtcdClient) clientInstance;
      try {
        writeToFileInClientBtcd(btcdClient, asset, account, number);
      } catch (Exception e) {
        log.error(asset + " createAddress exception: ", e);
        throw new ApiCallException(e.getMessage());
      }
    } else if (clientInstance instanceof Admin) {
      Admin ethClient = (Admin) clientInstance;
      try {
        writeToFileInClientWeb3(ethClient, asset, number, passPhrase);
      } catch (Exception e) {
        log.error(asset + " createAddress exception: ", e);
        throw new ApiCallException(e.getMessage());
      }
    } else if (clientInstance instanceof QtumClient) {
      QtumClient qtumClient = (QtumClient) clientInstance;
      try {
        writeToFileInClientQtum(qtumClient, asset, account, number);
      } catch (Exception e) {
        log.error(asset + " createAddress exception: ", e);
        throw new ApiCallException(e.getMessage());
      }
    } else if (clientInstance == null) {
      AchainClient achainClient = (AchainClient) clientInstance;
      try {
        writeToFileForAchainAsset(asset, number, account);
      } catch (Exception e) {
        log.error(asset + " createAddress exception: ", e);
        throw new ApiCallException(e.getMessage());
      }
    }
  }

  private void writeToFileInClientQtum(QtumClient qtumClient, String asset, String account,
      Integer number) throws Exception {

    BufferedWriter bufWriter = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.txt"));
    BufferedWriter bufWriter1 = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.sql"));
    for (int i = 0; i < number; i++) {
      String newAddress = qtumClient.getNewAddress(account);
      String privateKey = qtumClient.dumpPrivKey(newAddress);
      StringBuilder sql;
      if (i == 0){
        sql = new StringBuilder("INSERT INTO `address_pool_" + asset.toLowerCase()
            + "` (`asset_code`, `coin_address`, `wallet_account`, `address_status`, `del_flag`, `create_date`, `update_date`)\n"
            + "     VALUES\n").append(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      } else if (i == (number - 1)){
        sql = new StringBuilder(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now());");
      } else {
        sql = new StringBuilder(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      }

      bufWriter.write("ADDRESS:" + newAddress + " :: " + "PRIVATE:" + privateKey);
      bufWriter.newLine();
      bufWriter.flush();
      bufWriter1.write(sql.toString());
      bufWriter1.newLine();
      bufWriter1.flush();
    }
    bufWriter.close();
    bufWriter1.close();
  }

  private void writeToFileForAchainAsset(String asset, Integer number,
      String account) throws IOException {

    BufferedWriter bufWriter = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.txt"));
    BufferedWriter bufWriter1 = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.sql"));
    for (int i = 0; i < number; i++) {
      String newAddress = genNewAddr(account);

      StringBuilder sql;
      if (i == 0){
        sql = new StringBuilder("INSERT INTO `address_pool_" + asset.toLowerCase()
            + "` (`asset_code`, `coin_address`, `wallet_account`, `address_status`, `del_flag`, `create_date`, `update_date`)\n"
            + "     VALUES\n").append(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      } else if (i == (number - 1)){
        sql = new StringBuilder(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now());");
      } else {
        sql = new StringBuilder(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      }

      bufWriter.write("ADDRESS:" + newAddress);
      bufWriter.newLine();
      bufWriter.flush();
      bufWriter1.write(sql.toString());
      bufWriter1.newLine();
      bufWriter1.flush();
    }
    bufWriter.close();
    bufWriter1.close();
  }

  private void writeToFileInClientWeb3(Admin ethClient, String asset, Integer number,
      String passPhrase) throws IOException {


    BufferedWriter bufWriter = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.txt"));
    BufferedWriter bufWriter1 = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.sql"));
    for (int i = 0; i < number; i++) {

      String address = ethClient.personalNewAccount(passPhrase).send().getAccountId();

      StringBuilder sql;
      if (i == 0){
        sql = new StringBuilder("INSERT INTO `address_pool_" + asset.toLowerCase()
            + "` (`asset_code`, `coin_address`, `wallet_account`, `address_status`, `del_flag`, `create_date`, `update_date`)\n"
            + "     VALUES\n").append(" ('" + asset + "','" + address + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      } else if (i == (number - 1)){
        sql = new StringBuilder(" ('" + asset + "','" + address + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now());");
      } else {
        sql = new StringBuilder(" ('" + asset + "','" + address + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      }

      bufWriter.write("ADDRESS:" + address);
      bufWriter.newLine();
      bufWriter.flush();
      bufWriter1.write(sql.toString());
      bufWriter1.newLine();
      bufWriter1.flush();
    }
    bufWriter.close();
    bufWriter1.close();


  }

  private void writeToFileInClientBtcd(BtcdClient btcdClient, String asset, String account,
      Integer number)
      throws Exception {

    //todo 原文件中已有内容的处理：如果第一次执行写入了文件，第二次执行不会继续，而是覆盖掉了原来的内容
    BufferedWriter bufWriter = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.txt"));
    BufferedWriter bufWriter1 = new BufferedWriter(
        new FileWriter(System.getenv("HOME") + "/" + asset + "Addresses.sql"));
    for (int i = 0; i < number; i++) {

      String newAddress = btcdClient.getNewAddress(account);
      String privateKey = btcdClient.dumpPrivKey(newAddress);

      StringBuilder sql;
      if (i == 0){
        sql = new StringBuilder("INSERT INTO `address_pool_" + asset.toLowerCase()
            + "` (`asset_code`, `coin_address`, `wallet_account`, `address_status`, `del_flag`, `create_date`, `update_date`)\n"
            + "     VALUES\n").append(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      } else if (i == (number - 1)){
        sql = new StringBuilder(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now());");
      } else {
        sql = new StringBuilder(" ('" + asset + "','" + newAddress + "', '" + asset.toLowerCase()
            + "', 'NEW', 'FALSE', now(), now()),");
      }

      bufWriter.write("ADDRESS:" + newAddress + " :: " + "PRIVATE:" + privateKey);
      bufWriter.newLine();
      bufWriter.flush();
      bufWriter1.write(sql.toString());
      bufWriter1.newLine();
      bufWriter1.flush();
    }
    bufWriter.close();
    bufWriter1.close();

  }

  private static String genNewAddr(String addr) {

    return new StringBuilder(addr).append(randomString(32)).toString();

  }

  private static String randomString(int length) {
    String val = "";
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

      if ("char".equalsIgnoreCase(charOrNum)) // 字符串
      {
        int choice = 97; //
        val += (char) (choice + random.nextInt(26));
      } else if ("num".equalsIgnoreCase(charOrNum)) // 数字
      {
        val += String.valueOf(random.nextInt(10));
      }
    }
    return val;
  }
}
