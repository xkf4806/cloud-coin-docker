package com.ourdax.coindocker;

import com.google.common.base.Splitter;
import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author think on 11/1/2018
 */
@Component
@Slf4j
public class ServerContext {

  private EnumSet<AssetCode> supportedCodes;

  @Autowired
  private Environment env;

  private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

  @PostConstruct
  public void init() {
    String enables = env.getProperty("asset.enable");
    if (StringUtils.isNotEmpty(enables)) {
      List<String> enableList = SPLITTER.splitToList(enables);
      List<AssetCode> supportedList = enableList.stream()
          .map(String::toUpperCase)
          .map(AssetCode::valueOf)
          .collect(Collectors.toList());
      supportedCodes = EnumSet.copyOf(supportedList);
    }
    log.info("Enabled assets: {}", supportedCodes);
  }

  public EnumSet<AssetCode> getSupportedAssets() {
    return supportedCodes;
  }
}
