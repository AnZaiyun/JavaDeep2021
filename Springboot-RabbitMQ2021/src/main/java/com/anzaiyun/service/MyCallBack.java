package com.anzaiyun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    /**
     * 交换机不管是否收到消息的一个回调方法
     * CorrelationData
     *  消息相关数据
     * ack
     *  交换机是否收到消息      */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData!=null?correlationData.getId():"";

        if (ack){
            log.info("交换机已收到id为{}的消息",id);
        }else {
            log.info("交换机未收到id为{}的消息，原因为{}",id,cause);
        }
    }

    /**
     * 当消息无法路由的时候的回调方法
     * @param returned
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        Message message = returned.getMessage();
        String exchange = returned.getExchange();
        String replyText = returned.getReplyText();
        String routingKey = returned.getRoutingKey();
        log.error(" 消息{},被交换机{}退回，退回原因:{},路由key:{}",new String(message.getBody()),exchange,replyText,routingKey);
    }
}
