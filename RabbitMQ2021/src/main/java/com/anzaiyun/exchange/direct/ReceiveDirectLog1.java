package com.anzaiyun.exchange.direct;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveDirectLog1 {
    private static final String EXCHANGE_NAME = "logs-direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue,EXCHANGE_NAME,"info");
        channel.queueBind(queue,EXCHANGE_NAME,"warning");

        System.out.println("等待接收消息,把接收到的消息打印在屏幕.....");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String msg = new String(delivery.getBody(), "UTF-8");
            System.out.println("控制台打印接收到的消息："+msg);
        };

        channel.basicConsume(queue,true,deliverCallback,consumerTag -> {
            System.out.println("接收消息失败。。。");
        });
    }
}
