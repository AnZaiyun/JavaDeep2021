package com.anzaiyun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConfirmQueueConsumer {

    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    public static final String BACKUP_WARNING_QUEUE_NAME = "backup.warning.queue";

    @RabbitListener(queues =  CONFIRM_QUEUE_NAME)
    public void receiveMsg(Message message){
        String msg = new String(message.getBody());
        log.info("接收到confirm.queue队列的消息：{}",msg);
    }

    @RabbitListener(queues = BACKUP_QUEUE_NAME)
    public void backupProduce(Message message){
        String msg = new String(message.getBody());
        log.info("备份队列，开始处理消息{}",msg);
    }

    @RabbitListener(queues = BACKUP_WARNING_QUEUE_NAME)
    public void backupWarning(Message message){
        String msg = new String(message.getBody());
        log.warn("消息{}未配置路由对应队列，已由备份队列处理",msg);
    }
}
