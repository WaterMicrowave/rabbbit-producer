package com.hlkj.producer;

import com.hlkj.producer.component.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(value = SpringRunner.class)
@SpringBootTest
public class ProducerApplicationTests {

    @Resource
    private RabbitSender rabbitSender;

    @Test
    public void sendMsg() throws Exception {
        Map<String, Object> properties = new HashMap();
        properties.put("name", "李向平");
        properties.put("age", 30);
        rabbitSender.send("测试消息~", properties);
        //可能刚刚发送消息出去，避免异步的ConfirmCallback由于资源关闭而出现clean channel shutdown; protocol method:
        Thread.sleep(10000);
    }

}
