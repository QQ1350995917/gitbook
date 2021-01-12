package pwd.java.gc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * pwd.java.gc@gitbook
 *
 * <h1>GC in metadata space</h1>
 *
 * -XX:MetaspaceSize=4m -XX:MaxMetaspaceSize=4m Error occurred during initialization of VM
 * OutOfMemoryError: Metaspace
 *
 * -XX:MetaspaceSize=5m -XX:MaxMetaspaceSize=5m no error
 *
 * date 2021-01-12 16:44
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class GCMetadataSpace {

    public static void main(String[] args) throws Exception {
        int index = 0;
        while (true) {
            System.out.println(index ++);
            buildData(new String());
        }
    }

    public static String buildData(Object bean) throws Exception {
        SerializeConfig CONFIG = new SerializeConfig();
        CONFIG.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.toJSONString(bean, CONFIG).toString();
    }
}
