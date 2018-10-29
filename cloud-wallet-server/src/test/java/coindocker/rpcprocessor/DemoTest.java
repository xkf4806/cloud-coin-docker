package coindocker.rpcprocessor;

import coindocker.domains.AssetDto;
import coindocker.domains.AssetDtoCopy;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

/**
 * Created by zhangjinyang on 2018/7/17.
 */
public class DemoTest {

  @Test
  public void test() {
    BigDecimal bigDecimal = BigDecimal.ZERO;
    for (int i = 0; i < 5; i++) {
      bigDecimal = bigDecimal.add(new BigDecimal(5));
    }
    System.out.println(bigDecimal);
  }

  @Test
  public void testBeanUtils() {

    AssetDtoCopy copy = new AssetDtoCopy();
    AssetDto dto = new AssetDto(1, "DFSD", "BCH", new BigDecimal("1.3454454340000000"),
        new BigDecimal("0.0084723640"));
    BeanUtils.copyProperties(dto, copy);
    copy.setAmountLock(dto.getAmountAvailable().toString());
    System.out.println(JSON.toJSONString(copy));
  }

}
