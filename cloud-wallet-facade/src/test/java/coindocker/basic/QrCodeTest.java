package coindocker.basic;

import com.google.zxing.WriterException;
import java.io.IOException;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/7/6.
 */
public class QrCodeTest {

  @Test
  public void testQrCodeGeneration() throws WriterException {
    String content="ACT7uuBPJKc9QVrMJeu9bGWE4Xg9hyvzEQrS";
//    String content="I Love You";
    String logUri="/Users/zhangjinyang/Documents/logfile.png";
    String home = System.getenv().get("HOME");
    String outFileUri= home + "/qrcodeFile.jpg";
    int[]  size=new int[]{430,430};
    String format = "jpg";

    try {
      new QRCodeFactory().CreatQrImage(content, format, outFileUri, logUri,size);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (WriterException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
