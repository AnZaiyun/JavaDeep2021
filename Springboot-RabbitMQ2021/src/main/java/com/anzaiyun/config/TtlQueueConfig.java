package com.anzaiyun.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TtlQueueConfig {
    public static final String X_EXCHANGE = "X";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";

    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String DEAD_LETTER_QUEUE = "QD";

    @Bean
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    @Bean
    public DirectExchange yDeadLetterExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue queueA(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //设置死信队列的路由
        args.put("x-dead-letter-routing-key","YD");
        args.put("x-message-ttl",10000);

        return QueueBuilder.nonDurable(QUEUE_A).withArguments(args).build();
    }

    /**
     * @Qualifier注解的用处：当一个接口有多个实现的时候，为了指名具体调用哪个类的实现。
     * @param queueA
     * @param xExchange
     * @return
     */
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    @Bean
    public Queue queueB(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //设置死信队列的路由
        args.put("x-dead-letter-routing-key","YD");
        args.put("x-message-ttl",40000);

        return QueueBuilder.nonDurable(QUEUE_B).withArguments(args).build();
    }

    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    @Bean
    public Queue queueDeadLetter(){
        return new Queue(DEAD_LETTER_QUEUE);
    }

    @Bean
    public Binding deadLetterBindingQD(@Qualifier("queueDeadLetter") Queue queueDeadLetter,
                                       @Qualifier("yDeadLetterExchange") DirectExchange yDeadLetterExchange){
        return BindingBuilder.bind(queueDeadLetter).to(yDeadLetterExchange).with("YD");
    }
}
