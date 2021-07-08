package com.anzaiyun.msgack;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class BasicAckProducer {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        try(Channel channel = RabbitMqUtils.getChannel()){
            boolean durable = true;
            /*
            durable:队列是否需要持久化
            需要注意的就是如果之前声明的队列不是持久化的，需要把原先队列先删除，或者重新 创建一个持久化的队列，不然就会出现错误
             */
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

            int prefetchCount = 1;
            channel.basicQos(prefetchCount);
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入信息：");
            while (scanner.hasNext()){
                String message = scanner.nextLine();

                /*
                MessageProperties.PERSISTENT_TEXT_PLAIN消息实现持久化
                 */
                channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
                System.out.println("生产者发出消息"+message);
            }
        }
    }
}
