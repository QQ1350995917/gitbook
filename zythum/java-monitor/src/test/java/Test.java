import java.io.File;
import lombok.extern.slf4j.Slf4j;

/**
 * PACKAGE_NAME@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-15 14:26
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Slf4j
public class Test {

  public static void initialize() {
    String tsDbFile = System.getProperty("user.dir") + File.separator + "spf4j-performance-monitoring.tsdb2";
    String tsTextFile = System.getProperty("user.dir") + File.separator + "spf4j-performance-monitoring.txt";
    log.info("\nTime Series DB (TSDB) : {}\nTime Series text file : {}",tsDbFile,tsTextFile);
    System.setProperty("spf4j.perf.ms.config","TSDB@" + tsDbFile + "," + "TSDB_TXT@" + tsTextFile);
  }


}
