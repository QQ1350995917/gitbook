package pwd.java.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

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
public class ProducerConfirm3 {

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

      // 建立连接
      Connection conn = factory.newConnection();
      // 创建消息通道
      channel = conn.createChannel();

      String msg = "Hello world, Rabbit MQ, Async Confirm";
      // 声明队列（默认交换机AMQP default，Direct）
      // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);

      // 用来维护未确认消息的deliveryTag
      final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

      // 这里不会打印所有响应的ACK；ACK可能有多个，有可能一次确认多条，也有可能一次确认一条
      // 异步监听确认和未确认的消息
      // 如果要重复运行，先停掉之前的生产者，清空队列
      channel.addConfirmListener(new ConfirmListener() {
        // 处理未确认的消息
        // deliverTag：交付标签，标识服务端处理到哪条消息了
        // multiple：是否批量处理模式
        @Override
        public void handleNack(long deliveryTag, boolean multiple) throws IOException {
          System.out.println("Broker未确认消息，标识：" + deliveryTag);
          if (multiple) {
            // headSet表示后面参数之前的所有元素，全部删除
            confirmSet.headSet(deliveryTag + 1L).clear();
          } else {
            confirmSet.remove(deliveryTag);
          }
          // 这里添加重发的方法
        }

        // 处理已确认的消息
        // multiple如果true，表示批量执行了deliveryTag这个值以前（小于deliveryTag的）的所有消息，
        // 如果为false的话表示单条确认
        @Override
        public void handleAck(long deliveryTag, boolean multiple) throws IOException {
          // 如果true表示批量执行了deliveryTag这个值以前（小于deliveryTag的）的所有消息，如果为false的话表示单条确认
          System.out.println(String.format("Broker已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
          if (multiple) {
            // headSet表示后面参数之前的所有元素，全部删除
            confirmSet.headSet(deliveryTag + 1L).clear();
          } else {
            // 只移除一个元素
            confirmSet.remove(deliveryTag);
          }
          System.out.println("未确认的消息:"+confirmSet);
        }
      });

      // 开启发送方确认模式
      channel.confirmSelect();
      for (int i = 0; i < 10; i++) {
        // 获取消息的唯一ID，之后要加入sortedSet
        long nextSeqNo = channel.getNextPublishSeqNo();
        // 发送消息
        // String exchange, String routingKey, BasicProperties props, byte[] body
        channel.basicPublish("", QUEUE_NAME, null, (msg +"-"+ i).getBytes());
        confirmSet.add(nextSeqNo);
      }
      System.out.println("所有消息:"+confirmSet);

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
