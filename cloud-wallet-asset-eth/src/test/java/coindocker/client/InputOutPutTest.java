package coindocker.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/8/10.
 */
public class InputOutPutTest {

  @Test
  public void test() throws Exception {

    BufferedReader reader = new BufferedReader(new FileReader("/Users/zhangjinyang/Documents/update.txt"));
    BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/zhangjinyang/Documents/update.sql"));
    String line = null;
    while ((line = reader.readLine())!=null){
      String[] splits = line.split(" ");
      String s = "update set txid='"+splits[1]+"' where outer_order_no='"+splits[0]+"';";
      writer.write(s);
      writer.newLine();
      writer.flush();

    }

    reader.close();
    writer.close();

  }

}
