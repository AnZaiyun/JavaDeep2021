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
    public static final String QUEUE_C = "QC";

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
        args.put("x-message-ttl",20000);

        return QueueBuilder.nonDurable(QUEUE_B).withArguments(args).build();
    }

    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    @Bean
    public Queue queueC(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //设置死信队列的路由
        args.put("x-dead-letter-routing-key","YD");

        /**
         * 这里不再设置队列级别的延迟，而是选择在发送消息时设置消息级别的延迟
         * 两个级别的延迟也可以同时设置，同时设置时，较小的那个值将被使用
         *
         * 但需要注意的是如果使用在消息属性上设置TTL 的方式，消息可能并不会按时“死亡“，
         * 因为消息是否过期是在即将投递到消费者之前判定的，而一般情况下mq接收到消息后便会立即投递，
         * 在同时接收到多个消息额情况下，RabbitMQ只会检查第一个消息是否过期，如果过期则丢到死信队列，
         * 如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行。
         */
        args.put("x-message-ttl",40000);
        return QueueBuilder.nonDurable(QUEUE_C).withArguments(args).build();
    }

    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
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
