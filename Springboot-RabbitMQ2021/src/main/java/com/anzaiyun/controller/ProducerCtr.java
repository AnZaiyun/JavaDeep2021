package com.anzaiyun.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RequestMapping("ttl")
@RestController
public class ProducerCtr {

    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg/{msg}/{ttl}")
    public String sendMsg(@PathVariable("msg") String msg,
                          @PathVariable("ttl") String ttl){
        log.info("当前时间：{}，发送一条信息给三个ttl队列：{}",new Date(),msg);
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列："+msg);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为20s的队列："+msg);

        // 设置消息级别的延迟
        rabbitTemplate.convertAndSend("X","XC","消息自定义延迟时间的队列："+msg,correlationData->{
            correlationData.getMessageProperties().setExpiration(ttl);
            return correlationData;
        });

        return "消息发送成功："+msg;
    }

    @GetMapping("/sendDelayedMsg/{msg}/{ttl}")
    public String sendDelayedMsg(@PathVariable("msg") String msg,
                                 @PathVariable("ttl") Integer ttl){

        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE_NAME,DELAYED_ROUTING_KEY,msg,correlationData->{
            correlationData.getMessageProperties().setDelay(ttl);
            //如果设置了setExpiration，消息先延时投递，投递后消息在存活延时时间，没设置则永久存活--这点不确定--
            correlationData.getMessageProperties().setExpiration(ttl.toString());
            return correlationData;
        });

        log.info("当前时间：{},发送一条延迟{}毫秒的信息给队列 delayed.queue:{}", new Date(),ttl, msg);

        return "(自定义延迟队列)消息发送成功："+msg;
    }

}
