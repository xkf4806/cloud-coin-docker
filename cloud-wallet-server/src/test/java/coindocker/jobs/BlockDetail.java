package coindocker.jobs;

import java.util.List;
import lombok.Data;

/**
 * Created by zhangjinyang on 2018/9/14.
 */
@Data
public class BlockDetail {

  private Integer confirmations;
  private String hash;
  private Integer height;
  private String previousBlockHash;
  private Long time;
  private List<String> tx;

}
