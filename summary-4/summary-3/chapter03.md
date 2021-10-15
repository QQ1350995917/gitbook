# 架构详解

## Producer

## Broker

### Exchange

#### Fanout

We were using a fanout exchange, which doesn't give us much flexibility - it's only capable of mindless broadcasting.

#### Direct

We will use a direct exchange instead. The routing algorithm behind a direct exchange is simple - a message goes to the queues whose binding key exactly matches the routing key of the message.

#### Topic

Messages sent to a topic exchange can't have an arbitrary routing_key - it must be a list of words, delimited by dots. The words can be anything, but usually they specify some features connected to the message. A few valid routing key examples: "stock.usd.nyse", "nyse.vmw", "quick.orange.rabbit". There can be as many words in the routing key as you like, up to the limit of 255 bytes.

The binding key must also be in the same form. The logic behind the topic exchange is similar to a direct one - a message sent with a particular routing key will be delivered to all the queues that are bound with a matching binding key. However there are two important special cases for binding keys:

* (star) can substitute for exactly one word.
* (hash) can substitute for zero or more words.

Topic exchange is powerful and can behave like other exchanges.

When a queue is bound with "#" (hash) binding key - it will receive all the messages, regardless of the routing key - like in fanout exchange.

When special characters, "\*" (star) and "#" (hash), aren't used in bindings, the topic exchange will behave just like a direct one.

#### Headers

### Queue

#### CallbackQueue

### Binding

## Consumer

## 参考资料

[https://www.rabbitmq.com/tutorials/tutorial-four-java.html](https://www.rabbitmq.com/tutorials/tutorial-four-java.html)
