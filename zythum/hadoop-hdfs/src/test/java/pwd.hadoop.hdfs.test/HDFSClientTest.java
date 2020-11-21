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
//        System.setProperty("hadoop.home.dir", "E:\\data\\hadoop");
        Configuration configuration = new Configuration();
//        fileSystem = FileSystem.get(new URI("hdfs://192.168.50.52:9000"), configuration, "root");
        fileSystem = FileSystem.get(new URI("hdfs://192.168.31.17:9000"), configuration, "root");
        System.out.println();
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
    public void mkdir() throws Exception {
        boolean result = fileSystem.mkdirs(new Path("/test"));
        log.info("创建文件夹结果：{}", result);
    }

    /**
     * 上传文件
     */
    @Test
    public void uploadFile() {
        try(InputStream input = new FileInputStream("C:\\Users\\Administrator\\Desktop\\wu\\WU2-out.txt");
            OutputStream output = fileSystem.create(new Path("/test/WU2-out.txt"));) {
            IOUtils.copy(input, output, 4096);
            log.error("上传文件成功");
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件
     */
    @Test
    public void downFile() {
        try (InputStream input = fileSystem.open(new Path("/test/WU2-out.txt"));
            OutputStream output = new FileOutputStream("E:\\tmp\\WU2-out.txt");){
            IOUtils.copy(input, output, 4096);
            log.error("下载文件成功");
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void deleteFile() throws Exception {
        String fileName = "hadoop.txt";
        boolean result = fileSystem.delete(new Path("/test/" + fileName), true);
        log.info("删除文件结果：{}", result);
    }

    /**
     * 遍历文件
     */
    @Test
    public void listFiles() throws Exception {
        FileStatus[] statuses = fileSystem.listStatus(new Path("/"));
        for (FileStatus file : statuses) {
            log.info("扫描到文件或目录，名称：{}，是否为文件：{}", file.getPath().getName(), file.isFile());
        }
    }



//
//    //文件是否存在
//fileSystem.exists(new Path(fileName));
////创建目录
//fileSystem.mkdirs(new Path(directorName));
////删除目录或文件,第二个参数表示是否要递归删除
//fileSystem.delete(new Path(name), true);
////获取当前登录用户在HDFS文件系统中的Home目录
//fileSystem.getHomeDirectory();
////文件重命名
//fileSystem.rename(new Path(oldName), new Path(newName));
////读取文件，返回的是FSDataInputStream
//fileSystem.open(new Path(fileName));
////创建文件，第二个参数表示文件存在时是否覆盖
//fileSystem.create(new Path(fileName), false);
////从本地目录上传文件到HDFS
//fileSystem.copyFromLocalFile(localPath, hdfsPath);
////获取目录下的文件信息，包含path，length，group，blocksize，permission等等
//fileSystem.listStatus(new Path(directorName));
////释放资源
//fileSystem.close();
////设置HDFS资源权限，其中FsPermission可以设置user、group等
//fileSystem.setPermission(new Path(resourceName), fsPermission);
////设置HDFS资源的Owner和group
//fileSystem.setOwner(new Path(resourceName), ownerName, groupName);
////设置文件的副本
//fileSystem.setReplication(new Path(resourceName), count);
}
