package com.ourdax.coindocker.common.clients.achain.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/1/31.
 */
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
@ToString
public class CodePrintable {

  private String printable_code;

  private List<String> printable_storage_properties;

  private String code_hash;

  private List<String> events;

  private List<String> abi;

  private List<String> offline_abi;
}
