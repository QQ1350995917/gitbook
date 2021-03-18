package pwd.java.jvm.gc;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * pwd.java.gc@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-04-29 21:32
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Test {

  public static void main(String[] args) throws Exception {
    // INSERT INTO `caiot_nx`.`ba_ip` (`qsip`, `dwmc`) VALUES ('9485', '61.243.115.86', '61.243.115.86', '宁夏骅泰专用汽车销售有限公司', '企业', '20200429', '1039364950', '1039364950');
    String ip = "/Users/pwd/Documents/ips";
    String com = "/Users/pwd/Documents/coms";

    BufferedReader ipReader = new BufferedReader(new FileReader(ip));
    BufferedReader comReader = new BufferedReader(new FileReader(com));
    for (int i=0;i<78;i++){
      String ipLine = ipReader.readLine();
      String comLine = comReader.readLine();
      System.out.println("INSERT INTO `caiot_nx`.`ba_ip` (`qsip`, `dwmc`) VALUES ('" + ipLine + "','" + comLine + "');");
    }


    ipReader.close();
    comReader.close();

  }
}
