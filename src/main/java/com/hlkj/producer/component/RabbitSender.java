package com.hlkj.producer.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 确认消息的回调监听接口，用于确认消息是否被broker所收到
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         * @param correlationData 作为一个唯一的标识
         * @param b 是否落盘成功（broker）
         * @param s 失败的一些异常信息
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println("消息确认结果:" + ack + ", correlationData:" + correlationData.getId());
            System.out.println("cause: " + cause);
        }
    };

    /**
     * 对外发送消息
     * @param message 具体的消息内容
     * @param properties 额外的属性
     * @throws Exception
     */
    public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders mhs = new MessageHeaders(properties);

        Message<?> msg = MessageBuilder.createMessage(message, mhs);
        //设置消息确认回调t
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //发送消息
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());//指定业务唯一id
        System.out.println("发送消息时生成的唯一id：" + correlationData.getId());
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                System.out.printf("---> post to do: " + message);
                return message;
            }
        };
        rabbitTemplate.convertAndSend("exchange-1", "springboot.rabbit", msg, messagePostProcessor, correlationData);
    }

}
