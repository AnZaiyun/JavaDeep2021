package com.anzaiyun.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmQueueConfig {
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    public static final String BACKUP_WARNING_QUEUE_NAME = "backup.warning.queue";

    // 声明业务 Exchange

    /**
     * 为队列设置死信交换机可以用来存储那些处理失败的消息,但对于不可路由消息根本没有机会进入到队列，因此无法使用死信队列来保存消息
     * 在RabbitMQ中，有一种备份交换机的机制存在，可以很好的应对这个问题
     *
     * 备份交换机可以理解为 RabbitMQ中交换机的“备胎”，当我们为某一个交换机声明一个对应的备份交换机时，就是为它创建一个备胎
     * 当交换机接收到一条不可路由消息时，将会把这条消息转发到备份交换机中，由备份交换机来进行转发和处理
     * 通常备份交换机的类型为Fanout，这样就能把所有消息都投递到与其绑定的队列中，然后我们在备份交换机下绑定一个队列
     * 这样所有那些原交换机无法被路由的消息，就会都 进入这个队列了。
     * 当然，我们还可以建立一个报警队列，用独立的消费者来进行监测和报警
     * @return
     */
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME).
                withArgument("alternate-exchange",BACKUP_EXCHANGE_NAME).build();
    }

    @Bean
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }


    // 声明确认队列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    @Bean
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    @Bean
    public Queue backupWarningQueue(){
        return QueueBuilder.durable(BACKUP_WARNING_QUEUE_NAME).build();
    }

    // 声明确认队列绑定关系
    @Bean
    public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
                                @Qualifier("confirmExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("key1");
    }

    @Bean
    public Binding backupQueueBinding(@Qualifier("backupQueue") Queue backupQueue,
                                      @Qualifier("backupExchange") FanoutExchange backupExchange){
        return BindingBuilder.bind(backupQueue).to(backupExchange);

    }

    @Bean
    public Binding backupWarningQueueBinding(@Qualifier("backupWarningQueue") Queue backupWarningQueue,
                                             @Qualifier("backupExchange") FanoutExchange backupExchange){
        return BindingBuilder.bind(backupWarningQueue).to(backupExchange);

    }
}
