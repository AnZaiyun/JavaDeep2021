package com.anzaiyun.exchange.fanout;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 接收消息保存在磁盘本地
 */
public class ReceiveFanoutLog2 {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue,EXCHANGE_NAME,"");

        System.out.println("等待接收消息,把接收到的消息保存在文件.....");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(), "UTF-8");
            File file = new File("F:\\BaiduNetdiskDownload\\RabbitMQ\\笔记\\rabbitmq_info.txt");
            FileUtils.writeStringToFile(file,message,"UTF-8");
            System.out.println("数据写入文件成功");
        };

        channel.basicConsume(queue,true,deliverCallback,consumerTag -> {
            System.out.println("接收消息失败(文件)。。。");
        });
    }
}
