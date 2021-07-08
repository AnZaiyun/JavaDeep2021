package com.anzaiyun.exchange.topic;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;

public class SenderTopicLog1 {
    private static final String EXCHANGE_NAME = "logs-topic";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            /**
             * 声明一个 exchange
             * 1.exchange 的名称
             * 2.exchange 的类型
             */
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            Map<String,String> msgMap =  new HashMap<>();
            msgMap.put("info.color.red","this is info.color.red msg");
            msgMap.put("info.color.black","this is info.color.black msg");
            msgMap.put("warning.color.red","this is warning.color.red msg");
            msgMap.put("warning.color.black","this is warning.color.black msg");
            msgMap.put("error.color.red","this is error.color.red msg");
            msgMap.put("error.color.black","this is error.color.black msg");
            msgMap.put("debug.color.red","this is debug.color.red msg");
            msgMap.put("debug.color.black","this is debug.color.black msg");

            for (Map.Entry<String,String> msg:msgMap.entrySet()){
                channel.basicPublish(EXCHANGE_NAME, msg.getKey(), null, msg.getValue().getBytes("UTF-8"));
                System.out.println("生产者发出消息:" + msg.getValue());
            }



        }
    }
}
