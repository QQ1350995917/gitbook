package pwd.hadoop.hdfs.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * pwd.hadoop.hdfs@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-11-19 15:15
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
@Slf4j
public class HDFSClientTest {

    private FileSystem fileSystem = null;

    @Before
    public void before() throws Exception {
//        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.8.3");
        Configuration configuration = new Configuration();
        fileSystem = FileSystem.get(new URI("hdfs://192.168.107.141:9000"), configuration, "root");
    }

    @After
    public void after() throws Exception {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }


    /**
     * 创建文件夹
     */
    @Test
    public void mkdir() {
        try {
            boolean result = fileSystem.mkdirs(new Path("/test"));
            log.info("创建文件夹结果：{}", result);
        } catch (IllegalArgumentException | IOException e) {
            log.error("创建文件夹出错", e);
        }
    }

    /**
     * 上传文件
     */
    @Test
    public void uploadFile() {
        String fileName = "hadoop.txt";
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream("F:\\" + fileName);
            output = fileSystem.create(new Path("/test/" + fileName));
            IOUtils.copy(input, output, 4096);
            log.error("上传文件成功");
        } catch (IllegalArgumentException | IOException e) {
            log.error("上传文件出错", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 下载文件
     */
    @Test
    public void downFile() {
        String fileName = "hadoop.txt";
        InputStream input = null;
        OutputStream output = null;
        try {
            input = fileSystem.open(new Path("/test/" + fileName));
            output = new FileOutputStream("F:\\down\\" + fileName);
            IOUtils.copy(input, output, 4096);
            log.error("下载文件成功");
        } catch (IllegalArgumentException | IOException e) {
            log.error("下载文件出错", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void deleteFile() {
        String fileName = "hadoop.txt";
        try {
            boolean result = fileSystem.delete(new Path("/test/" + fileName), true);
            log.info("删除文件结果：{}", result);
        } catch (IllegalArgumentException | IOException e) {
            log.error("删除文件出错", e);
        }
    }

    /**
     * 遍历文件
     */
    @Test
    public void listFiles() {
        try {
            FileStatus[] statuses = fileSystem.listStatus(new Path("/"));
            for (FileStatus file : statuses) {
                log.info("扫描到文件或目录，名称：{}，是否为文件：{}", file.getPath().getName(), file.isFile());
            }
        } catch (IllegalArgumentException | IOException e) {
            log.error("遍历文件出错", e);
        }
    }

}
