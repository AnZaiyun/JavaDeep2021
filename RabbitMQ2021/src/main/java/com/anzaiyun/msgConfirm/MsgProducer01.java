package com.anzaiyun.msgConfirm;

import com.anzaiyun.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * 消息发布确认模式
 */
public class MsgProducer01 {
    private static final String QUEUE_NAME = "confirm_queue";
    private static final int MESSAGE_COUNT = 1000;

    /**
     * 单个确认发布
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public void publishMessageSingle() throws IOException, TimeoutException, InterruptedException {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 开启发布确认
            channel.confirmSelect();
            long begin = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                // 服务端返回 false 或超时时间内未返回，生产者可以消息重发
                boolean flag = channel.waitForConfirms();
                if(flag){
                    //System.out.println("消息发送成功");
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) + "ms");
        }
    }

    /**
     * 批量发布确认
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public void publishMessageMutil() throws IOException, TimeoutException, InterruptedException {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 开启发布确认
            channel.confirmSelect();
            int mutilCount = 100;
            long begin = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                // 服务端返回 false 或超时时间内未返回，生产者可以消息重发
                if ((i+1)%mutilCount==0) {
                    boolean flag = channel.waitForConfirms();
                    if (flag) {
                        System.out.println("消息发送成功");
                    }
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) + "ms");
        }
    }

    /**
     * 异步发布确认
     * @throws Exception
     */
    public void  publishMessageAsync() throws Exception{
        try(Channel channel = RabbitMqUtils.getChannel()){
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            channel.confirmSelect();

            /**
             * 线程安全有序的一个哈希表，适用于高并发的情况
             * 1. 轻松的将序号与消息进行关联
             * 2. 轻松批量删除条目 只要给到序列号
             * 3. 支持并发访问
             */
            ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
            ConfirmCallback ackCallback = (seqNumber,multiple)->{
                if (multiple){
                    // 返回的是小于等于当前序列号的未确认消息 是一个 map
                    ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(seqNumber, true);
                    // 清除该部分未确认消息
                    confirmed.clear();
                }else {
                    // 只清除当前序列号的消息
                    outstandingConfirms.remove(seqNumber);
                }
            };
            ConfirmCallback nackCallback = (sequenceNumber, multiple) -> {
                String message = outstandingConfirms.get(sequenceNumber);
                System.out.println("发布的消息"+message+"未被确认，序列号"+sequenceNumber);
            };
            /**
             * 添加一个异步确认的监听器
             * 1. 确认收到消息的回调
             * 2. 未收到消息的回调
             */
            channel.addConfirmListener(ackCallback, nackCallback);

            long begin = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = "消息" + i;
                /**
                 * * channel.getNextPublishSeqNo() 获取下一个消息的序列号
                 * * 通过序列号与消息体进行一个关联
                 * * 全部都是未确认的消息体
                 * */
                outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }
            long end = System.currentTimeMillis();
            System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时" + (end - begin) + "ms");
        }

    }

    public static void main(String[] args) {
        MsgProducer01 msgProducer01 = new MsgProducer01();
        try {
            msgProducer01.publishMessageSingle();
            msgProducer01.publishMessageMutil();
            msgProducer01.publishMessageAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
