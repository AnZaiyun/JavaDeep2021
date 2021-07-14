package com.anzaiyun.controller;

import com.anzaiyun.service.MyCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Slf4j
@RequestMapping("confirm")
@RestController
public class ConfirmProducerCtr {

    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MyCallBack myCallBack;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(myCallBack);
        /**
         *true:交换机无法将消息进行路由时，会将该消息返回给生产者
         *      如果设置了备份交换机，则会将消息传给备份交换机，一般情况下备份交换机都是fanout类型，会将消息传递给所有绑定的队列
         *      因此在配置了备份交换的情况下，只有在配置的备份交换机也出问题的情况下，才会调用传递失败函数
         *false:如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(myCallBack);
    }

    @RequestMapping("/sendMsg/{msg}")
    public String sendMsg(@PathVariable("msg") String msg){
        CorrelationData correlationData1 = new CorrelationData("1");
        CorrelationData correlationData2 = new CorrelationData("2");

        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME,"key1",msg,correlationData1);
        log.info("已发送消息：{}，id为：{}，路由为：{}",msg,1,"key1");
        //当前路由规则无队列绑定
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME,"key2",msg,correlationData2);
        log.info("已发送消息：{}，id为：{}，路由为：{}",msg,2,"key2");


        return "已发送消息："+msg;
    }
}
