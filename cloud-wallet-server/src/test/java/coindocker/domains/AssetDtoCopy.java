package coindocker.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zhangjinyang on 2018/7/20.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDtoCopy {

  private Integer userId;

  private String accountNo;

  private String assetCode;

  private String amountAvailable;

  private String amountLock;

}
