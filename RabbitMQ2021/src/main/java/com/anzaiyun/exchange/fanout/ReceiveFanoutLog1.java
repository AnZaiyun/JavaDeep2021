package com.anzaiyun.exchange.fanout;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveFanoutLog1 {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue,EXCHANGE_NAME,"");

        System.out.println("等待接收消息,把接收到的消息打印在屏幕.....");

        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            String msg = new String(delivery.getBody(), "UTF-8");
            System.out.println("控制台打印接收到的消息："+msg);
        };

        channel.basicConsume(queue,true,deliverCallback,consumerTag -> {
            System.out.println("接收消息失败。。。");
        });
    }
}
