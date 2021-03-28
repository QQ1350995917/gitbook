package pwd.java.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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
public class ProducerConfirm2 {

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

      //批量确认就是在开启Confirm模式后，先发送一批消息。只要channel.waitForConfirmsOrDie()方法没有抛出异常，就代表消息都被服务端接收了。
      //批量确认的方式比单条确认的方式效率要高，但是对于不同的业务，到底发送多少条消息确认一次？
      //数量太少，效率提升不上去.
      //数量多的话，又会带来另一个问题，比如我们发1000条消息才确认一次，如果前面999 条消息都被服务端接收了，如果第1000条消息被拒绝了，那么前面所有的消息都要重发。


      // 建立连接
      Connection conn = factory.newConnection();
      // 创建消息通道
      channel = conn.createChannel();

      String msg = "Hello world, Rabbit MQ ,Batch Confirm";
      // 声明队列（默认交换机AMQP default，Direct）
      // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);

      try {
        channel.confirmSelect();
        for (int i = 0; i < 5; i++) {
          // 发送消息
          // String exchange, String routingKey, BasicProperties props, byte[] body
          channel.basicPublish("", QUEUE_NAME, null, (msg +"-"+ i).getBytes());
        }
        // 批量确认结果，ACK如果是Multiple=True，代表ACK里面的Delivery-Tag之前的消息都被确认了
        // 比如5条消息可能只收到1个ACK，也可能收到2个（抓包才看得到）
        // 直到所有信息都发布，只要有一个未被Broker确认就会IOException
        channel.waitForConfirmsOrDie();
        System.out.println("消息发送完毕，批量确认成功");
      } catch (Exception e) {
        // 发生异常，可能需要对所有消息进行重发
        e.printStackTrace();
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
