package coindocker.basic;

import com.ourdax.coindocker.common.enums.AssetCode;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/5/21.
 */
public class EnumTest {

  @Test
  public void testBelong(){
    System.out.println(AssetCode.EOS.isERC20());
  }

}
