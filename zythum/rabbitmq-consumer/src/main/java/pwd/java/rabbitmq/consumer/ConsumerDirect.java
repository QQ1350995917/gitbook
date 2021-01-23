package pwd.java.rabbitmq.consumer;

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
public class ConsumerDirect {

  public static void main(String[] args) throws InterruptedException, MQClientException {

    // Instantiate with specified consumer group name.
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_name");

    // Specify name server addresses.
    consumer.setNamesrvAddr("localhost:9876");

    // Subscribe one more more topics to consume.
    consumer.subscribe("TopicTest", "*");
    // Register callback to execute on arrival of messages fetched from brokers.
    consumer.registerMessageListener(new MessageListenerConcurrently() {

      @Override
      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
          ConsumeConcurrentlyContext context) {
        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      }
    });

    //Launch the consumer instance.
    consumer.start();

    System.out.printf("ConsumerDirect Started.%n");
  }

}
