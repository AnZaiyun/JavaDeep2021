package com.anzaiyun.msgack;

import com.anzaiyun.utils.RabbitMqUtils;
import com.anzaiyun.utils.SleepUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BasicAckConsumer {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C1等待接收消息。。。业务处理时间短。。。");
        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            String msg = new String(delivery.getBody());
            SleepUtils.sleep(1);
            System.out.println("C1接收到消息："+msg);

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };

        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,(consumerTag)->{
            System.out.println("C1回调逻辑");
        });
    }


}
