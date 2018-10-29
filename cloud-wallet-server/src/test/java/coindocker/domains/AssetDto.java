package coindocker.domains;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zhangjinyang on 2018/7/20.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDto {

  private Integer userId;

  private String accountNo;

  private String assetCode;

  private BigDecimal amountAvailable;

  private BigDecimal amountLock;

}
