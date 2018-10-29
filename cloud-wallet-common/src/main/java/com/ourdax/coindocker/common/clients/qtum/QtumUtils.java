package com.ourdax.coindocker.common.clients.qtum;

import java.math.BigDecimal;

/**
 * Created by zhangjinyang on 2018/5/3.
 */
public class QtumUtils {

  public static final Integer SIXTY_FOUR = 64;
  public static final Integer FOURTY = 40;

  public static String completeStringTo64(String str, Integer size) {

    StringBuilder result = new StringBuilder();
    int shortLenth = (size - str.length());

    for (int i = 0; i < shortLenth; i++) {
      result.append("0");
    }
    result.append(str);
    return result.toString();

  }

  public static String trimHexString(String hexStr) {

    char[] chars = hexStr.toCharArray();
    int count = 0;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == '0') {
        count++;
      } else {
        break;
      }
    }
    return hexStr.substring(count, hexStr.length());

  }

  public static String decimalToHex(BigDecimal amount, Integer decimal) {

    String amountHex = Long.toHexString(amount.multiply(new BigDecimal(Math.pow(10, decimal))).longValue());
    return completeStringTo64(amountHex, SIXTY_FOUR);

  }

  public static BigDecimal hexToDecimal(String amountHex, Integer decimal) {

    BigDecimal amount = hexToDecimal(amountHex);
    return amount.divide(new BigDecimal(Math.pow(10, decimal)))
        .stripTrailingZeros();

  }

  /**
   *16进制字符串转换为10进制数字
   * @param s
   * @return
   */
  public static BigDecimal hexToDecimal(String s) {

    String pureHexString = trimHexString(s);
    int lastindex = pureHexString.length() - 1;
    double result = 0;
    for (int i = lastindex; i >= 0; i--) {

      int digit = Character.digit(pureHexString.charAt(i), 16);
      double decimalDigit = digit * Math.pow(16, (lastindex - i));
      result += decimalDigit;
    }
    return new BigDecimal(result);

  }

//  public static void main(String[] args) {
//
//    System.out.println(hexToDecimal("8a6e51a6728580000", 18));
//
//  }

}
