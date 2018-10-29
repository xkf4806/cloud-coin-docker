package com.ourdax.coindocker.common.clients.qtum.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/5/3.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(
    ignoreUnknown = true
)
public class SearchLog {

  private String address;
  private List<String> topics;
  private String data;

}
