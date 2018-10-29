package coindocker;

import com.ourdax.coindocker.common.enums.AssetCode;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/4/28.
 */
public class AssetCodeTest {

  @Test
  public void testEnum(){
    System.out.println(AssetCode.EOS.isERC20());
  }

  @Test
  public void testHash(){
    String s = "5198fd0fd2ec1282fc1511cf588480a9cfe61442";
    System.out.println(s.length());
    StringBuilder b = new StringBuilder();
    for (int i=0; i<24; i++){
      b.append("0");
    }
    b.append(s);
    System.out.println(b.toString());
    System.out.println(b.length());
  }

  @Test
  public void testScript(){
    String s = "000000000000000000000000000000000000000000000000000000003b9aca00";
    System.out.println(s.length());
  }

}
