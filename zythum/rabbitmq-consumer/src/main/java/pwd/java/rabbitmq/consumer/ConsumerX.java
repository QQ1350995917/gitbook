package pwd.java.rabbitmq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

/**
 * pwd.java.rabbitmq.consumer@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-01-23 21:47
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ConsumerX {
  public static void main(String[] args) {
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
      connection = factory.newConnection();
      channel = connection.createChannel();

      // 声明交换机类型
      channel.exchangeDeclare(exchangeName, "fanout", true);
      // 声明默认的队列(也可不申明队列，使用默认队列)
      channel.queueDeclare(queneName, true, false, true, null);
      // String queue = channel.queueDeclare().getQueue();
      // 将队列与交换机绑定
      channel.queueBind(queneName, exchangeName, routingKey);

      Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
            byte[] body) throws IOException {
          String message = new String(body, "UTF-8");
          System.out.println(envelope.getExchange() + "," + envelope.getRoutingKey() + "," + message);
        }
      };
      // channel绑定队列、消费者，autoAck为true表示一旦收到消息则自动回复确认消息
      channel.basicConsume(queneName, true, consumer);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
