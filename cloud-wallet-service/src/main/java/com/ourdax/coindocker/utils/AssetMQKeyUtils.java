package com.ourdax.coindocker.utils;

import com.ourdax.coindocker.common.enums.AssetCode;
import java.text.MessageFormat;

/**
 * @author think on 13/1/2018
 */
public class AssetMQKeyUtils {

  private AssetMQKeyUtils() {}

  public static String getKeyFor(AssetCode code, String pattern) {
    MessageFormat format = new MessageFormat(pattern);
    return format.format(new Object[] {code.name().toLowerCase()});
  }
}
