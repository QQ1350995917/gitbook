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
public class ProducerTX {

  private final static String QUEUE_NAME = "ORIGIN_QUEUE";

  public static void main(String[] args) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setPort(5672);
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setVirtualHost("test_vhosts");

    Connection conn = factory.newConnection(); // 建立连接
    Channel channel = conn.createChannel(); // 创建消息通道

    String msg = "Hello world, Rabbit MQ";
    // 声明队列（默认交换机AMQP default，Direct）
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    try {
      channel.txSelect(); // 事务模式
      // 发送消息
      // String exchange, String routingKey, BasicProperties props, byte[] body
      channel.basicPublish("", QUEUE_NAME, null, (msg).getBytes());
      // int i =1/0;
      channel.txCommit(); // 提交，阻塞
      System.out.println("消息发送成功");
    } catch (Exception e) {
      channel.txRollback(); // 回滚
      System.out.println("消息已经回滚");
    }
    channel.close();
    conn.close();
  }

//  在事务模式里面，只有收到了服务端的 Commit-OK的指令，才能提交成功。所以可以解决生产者和服务端确认的问题。但是事务模式有一个缺点，它是阻塞的，一条消息没有发送完毕，不能发送下一条消息，它可能会榨干RabbitMQ服务器的性能。所以不建议在生产环境使用。
//
//  使用SpringAMQP时，在构造RabbitTemplate的Bean时设置，因为RabbitTemplate封装了channel
//
//  rabbitTemplate.setChannelTransacted(true);

}
