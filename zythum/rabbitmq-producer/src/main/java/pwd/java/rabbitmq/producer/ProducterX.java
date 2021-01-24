package pwd.java.rabbitmq.producer;

import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * pwd.java.rabbitmq.producer@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-01-23 21:47
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ProducterX {
  public static void main(String[] args) throws IOException, TimeoutException {

    String queneName = "firstQueue";
    String exchangeName = "amq.fanout";
    String routingKey = "test1";
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
      // 声明交换机类型
      channel.exchangeDeclare("amq.fanout", "fanout", true);
      // 声明默认的队列 (也可不申明队列，使用默认队列)
      boolean durable = true;
      channel.queueDeclare(queneName, durable, false, true, null);
      // String queue = channel.queueDeclare().getQueue();
      // 将队列与交换机绑定
      channel.queueBind(queneName, exchangeName, routingKey);
      // 指定一个队列
      // channel.queueDeclare(queneName, false, false, false, null);
      while (true) {
        channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, UUID.randomUUID().toString().getBytes());
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
