package coindocker.jobs;

import com.ourdax.coindocker.common.utils.AddressUtils;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/7/4.
 */
public class AddressMatchTest {

  @Test
  public void testAddressUtil(){
    System.out.println(AddressUtils.validateAchainAddr("ACT7uuBPJKc9QVrMJeu9bGWE4Xg9hyvzEQrS3uj5al53w8t6i3466n98l374d6mpf27o"));
  }

  @Test
  public void testAchainAddr(){
    System.out.println("ACTnCnvfEU1Cpf6HXJjRzN8D5SrWc64azbWD".length());
  }

}
