package com.anzaiyun.deadMsg;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MsgSender {
    private static final String EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        try(Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();

            for(int i = 0; i < 10; i++){
                String msg = "info"+i;
                channel.basicPublish(EXCHANGE_NAME,"zhangsan",properties,msg.getBytes());
                System.out.println("生产者发送消息："+msg);
            }

        }
    }
}
