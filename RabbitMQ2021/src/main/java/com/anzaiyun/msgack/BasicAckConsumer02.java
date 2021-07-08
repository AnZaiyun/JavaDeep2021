package com.anzaiyun.msgack;

import com.anzaiyun.utils.RabbitMqUtils;
import com.anzaiyun.utils.SleepUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BasicAckConsumer02 {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C2等待接收消息。。。业务处理时间长。。。");
        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String msg = new String(delivery.getBody());
            SleepUtils.sleep(30);
            System.out.println("C2接收到消息："+msg);

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };
        /*
        不公平分发，设置预取值的大小，当当前消费者处于繁忙状态时，预取值空间也已满，mq不会再将消息发给当前消费者，而是发给空闲消费者
         */
        channel.basicQos(1);
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,(consumerTag)->{
            System.out.println("C2回调逻辑");
        });
    }
}
