package coindocker.rpcprocessor;

import coindocker.AbstractTestCase;
import com.google.common.util.concurrent.Uninterruptibles;
import com.ourdax.coindocker.common.utils.JsonUtils;
import com.ourdax.coindocker.mq.MQProducer;
import com.ourdax.coindocker.mq.Message;
import com.ourdax.coindocker.mq.messages.TransferOutRequest;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 11/1/2018
 */
public class MessageListenerTest extends AbstractTestCase {

  @Autowired
  private MQProducer mqProducer;


  @Before
  public void prepareMessage() {
    for (int i = 0; i < 1; i++) {
      TransferOutRequest request = JsonUtils.parse(
          "{\"address\":\"0xbac9573be9c1f381c3789384b9afb851d300a4a5\",\"amount\":\"0.99000000000000000000\",\"message\":\"message\",\"txfee\":\"0.01000000000000000000\",\"txid\":\"201711030000100131781224551009\"}",
          TransferOutRequest.class);

      Message message = new Message();
      message.setExchange("gdae2.gopMarketExchange");
      message.setRouterKey("gdae2.transferOut.eos.key");
      message.setData(request);
      mqProducer.sendMessage(message);

      Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MINUTES);
    }
  }


  @Test
  public void testConsumeMessage() {
    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.HOURS);
  }
}
