package com.ourdax.coindocker.utils;

import java.util.Random;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by zhangjinyang on 2018/2/5.
 */

public class GenerateAddress {


  public static String generateAddress(Integer length){

    byte[] bytes = new byte[length];
    new Random().nextBytes(bytes);
    return DatatypeConverter.printHexBinary(bytes).toLowerCase();

  }

  public static void main(String[] args) {
    String s = "83f91c5c98530634f2ac87e3254a90bf";
    System.out.println(s.length()/2);

    String s1 = GenerateAddress.generateAddress(s.length()/2);
    System.out.println(s1);
  }
}
