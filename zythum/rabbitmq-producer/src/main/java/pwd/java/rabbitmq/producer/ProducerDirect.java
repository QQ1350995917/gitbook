package pwd.java.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-24 16:51
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ProducerDirect {


  public static void main(String[] args) throws IOException, TimeoutException {

    String queneName = "testQuene";
    Connection connection = null;
    Channel channel = null;
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      factory.setPort(5672);
      factory.setUsername("guest");
      factory.setPassword("guest");
      factory.setVirtualHost("test_vhosts");
      // 创建与RabbitMQ服务器的TCP连接
      connection = factory.newConnection();
      // 创建一个频道
      channel = connection.createChannel();
      // 声明默认的队列
      channel.queueDeclare(queneName, true, false, true, null);
      while (true) {
        channel.basicPublish("", queneName, null, UUID.randomUUID().toString().getBytes());
        Thread.sleep(1000);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (channel != null) {
        channel.close();
      }
      if (connection != null) {
        connection.close();
      }
    }
  }
}
