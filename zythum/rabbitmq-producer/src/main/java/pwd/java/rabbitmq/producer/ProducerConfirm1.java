package pwd.java.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.UUID;

/**
 * pwd.java.rabbitmq.producer@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-03-28 22:51
 *
 * @author DingPengwei[www.dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class ProducerConfirm1 {

  private final static String QUEUE_NAME = "ORIGIN_QUEUE";

  public static void main(String[] args) throws Exception {
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

      //在生产者这边通过调用channel.confirmSelect()方法将信道设置为Confirm模式，然后发送消息。一旦消息被投递到所有匹配的队列后，RabbitMQ 就会发送一个确认（Basic.Ack）给生产者，也就是调用 channel.waitForConfirms()返回 true，这样生产者就知道消息被服务端接收了。


      // 建立连接
      Connection conn = factory.newConnection();
      // 创建消息通道
      channel = conn.createChannel();

      String msg = "Hello world, Rabbit MQ ,Normal Confirm";
      // 声明队列（默认交换机AMQP default，Direct）
      // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);

      // 开启发送方确认模式
      channel.confirmSelect();

      channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
      // 普通Confirm，发送一条，确认一条
      //这种发送1条确认1条的方式消息还不是太高，所以还有一种批量确认的方式。
      if (channel.waitForConfirms()) {
        System.out.println("消息发送成功" );
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
