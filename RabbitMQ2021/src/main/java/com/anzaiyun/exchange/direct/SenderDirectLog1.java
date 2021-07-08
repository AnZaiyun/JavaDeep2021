package com.anzaiyun.exchange.direct;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class SenderDirectLog1 {
    private static final String EXCHANGE_NAME = "logs-direct";
    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            /**
             * 声明一个 exchange
             * 1.exchange 的名称
             * 2.exchange 的类型
             */
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String msg = "this is info msg";
            channel.basicPublish(EXCHANGE_NAME, "info", null, msg.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + msg);

            msg = "this is warning msg";
            channel.basicPublish(EXCHANGE_NAME, "warning", null, msg.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + msg);

            msg = "this is error msg";
            channel.basicPublish(EXCHANGE_NAME, "error", null, msg.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + msg);

            msg = "this is debug msg";
            channel.basicPublish(EXCHANGE_NAME, "debug", null, msg.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + msg);

        }
    }
}
