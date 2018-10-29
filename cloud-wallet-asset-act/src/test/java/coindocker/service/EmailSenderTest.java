package coindocker.service;

import coindocker.ActAbstractTestCase;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.domain.EmailObj;
import com.ourdax.coindocker.service.EmailService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhangjinyang on 2018/8/21.
 */
public class EmailSenderTest extends ActAbstractTestCase{

  @Autowired
  private EmailService emailService;

  @Test
  public void testEmailService(){
    EmailObj email = new EmailObj();
    email.setSubject("测试邮件");
    email.setText("这是一封测试邮件");
    email.setToUser(Lists.newArrayList("zhangjingang@new4g.cn"));
    emailService.sendEmail(email);
  }

}
