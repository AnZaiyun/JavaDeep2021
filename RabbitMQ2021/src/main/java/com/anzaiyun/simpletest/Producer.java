package com.anzaiyun.simpletest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.11");
        factory.setUsername("admin");
        factory.setPassword("123");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
        ){
            /**
             * 生成一个队列
             * 1. 队列名称
             * 2. 队列里面的消息是否持久化 默认消息存储在内存中
             * 3. 该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
             * 4. 是否自动删除 最后一个消费者端开连接以后该队列是否自动删除 true 自动删除
             * 5. 其他参数
             */
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            String msg = "hello world";
            channel.basicPublish("",QUEUE_NAME,null,msg.getBytes());
            System.out.println("消息发送完毕");
        }
    }
}
