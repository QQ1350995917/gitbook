package pwd.spring.mybatis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import pwd.spring.mybatis.mapper.ListMapMapper;

/**
 * pwd.spring.mybatis@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2019-12-05 9:16
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class InsertBlackDomainListFromTxt {

  public static void main(String[] args) throws Exception {
    String resource = "mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    SqlSession session = sqlSessionFactory.openSession(true);
    ListMapMapper listMapMapper = session.getMapper(ListMapMapper.class);

    String filePath = "F:\\交接文档\\湖南物联网\\host.txt";
    Long start = System.currentTimeMillis();
    List<Map<String, String>> maps = readFileByLine(filePath);
    listMapMapper.insert(maps);
    System.out.println("duration:" + (System.currentTimeMillis() - start));
  }

  public static List<Map<String,String>> readFileByLine(String path) throws IOException {
    List<Map<String,String>> list = new ArrayList<>();
    FileInputStream fis = new FileInputStream(path);
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
    BufferedReader br = new BufferedReader(isr);
    String line = "";
    while ((line = br.readLine()) != null) {
      if (line.contains(",")) {
        String[] split = line.split(",");
        for (String s : split) {
          Map<String,String> map = new HashMap<String, String>(1);
          map.put("domain",s);
          list.add(map);
        }
      } else {
        Map<String,String> map = new HashMap<String, String>(1);
        map.put("domain",line);
        list.add(map);
      }
    }
    br.close();
    isr.close();
    fis.close();
    return list;
  }

}
