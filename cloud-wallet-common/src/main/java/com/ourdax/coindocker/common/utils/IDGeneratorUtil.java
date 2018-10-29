package com.ourdax.coindocker.common.utils;

import java.util.Random;
import java.util.UUID;

/**
 * Created by wangyinbin on 2016/12/23.
 */
public class IDGeneratorUtil {

  public static String generateClientId() {

    StringBuilder clientId = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      clientId.append(new Random().nextInt(10));
    }
    return clientId.toString();

  }

  public static String getUUID() {

    return UUID.randomUUID().toString().replaceAll("-", "");

  }

  //    public static String generateUserID(){
//
//        IDGenerator generator = new IDGenerator();
//        Long userId = generator.next();
//        return userId.toString();
//    }
  public static void main(String[] args) {
    System.out.println(generateClientId());
  }


}
