package pwd.java.jdbc.test;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import javax.sql.DataSource;

/**
 * pwd.java.jdbc.test@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-09-24 10:41
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class JDBCTest {

    static String url = "jdbc:mysql://localhost:3306/initializr_account";
    static String user = "root";
    static String pwd = "root";
    static Integer times = 10;
    static String sql = "select count(*) as result from admin_account";



    public static void main(String[] args) throws Exception {
//        new JDBCTest().jdbc();
//        new JDBCTest().druid();
        new JDBCTest().druidPred();
    }

    //    @org.junit.JDBCTest
    public void jdbc() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(times);
        for (int i = 0; i < times; i++) {
            new Thread(() -> {
                try {
                    Long start = System.currentTimeMillis();
                    Connection connection = DriverManager.getConnection(url, user, pwd);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet.next()) {
                        String result = resultSet.getString("result");
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                    Long end = System.currentTimeMillis();
                    System.out.println(end - start);
                    countDownLatch.countDown();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        countDownLatch.await();
    }

    //    @org.junit.JDBCTest
    public void druid() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(times);
        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("username", user);
        properties.setProperty("password", pwd);
        properties.setProperty("maxActive", 5 + "");
        properties.setProperty("initialSize", 5 + "");
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        for (int i = 0; i < times; i++) {
            new Thread(() -> {
                try {
                    Long start = System.currentTimeMillis();
                    Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet.next()) {
                        String result = resultSet.getString("result");
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                    Long end = System.currentTimeMillis();
                    System.out.println(end - start);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }

    public void druidPred() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(times);
        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("username", user);
        properties.setProperty("password", pwd);
        properties.setProperty("maxActive", 5 + "");
        properties.setProperty("initialSize", 5 + "");
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        for (int i = 0; i < times; i++) {
            new Thread(() -> {
                try {
                    Long start = System.currentTimeMillis();
                    Connection connection = dataSource.getConnection();
                    System.out.println(connection);
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    boolean execute = preparedStatement.execute();
                    preparedStatement.close();
                    connection.close();
                    Long end = System.currentTimeMillis();
                    System.out.println(end - start);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }
}
