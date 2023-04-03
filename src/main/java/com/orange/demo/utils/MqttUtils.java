package com.orange.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.demo.factory.MqttFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

/**
 * Li ZhiCheng
 * 2023-03-2023/3/8 13:34
 * Mqtt工具类
 */
@Slf4j
public class MqttUtils {

    //主题
    private final static String TOPIC = "v1/devices/me/telemetry";

    /**
     *   发送消息
     *   @param data 消息内容
     */
    public static void send(String clientId,Object data) throws JsonProcessingException, MqttException {
        // 获取客户端实例
        MqttClient client = MqttFactory.getInstance(clientId);
        ObjectMapper mapper = new ObjectMapper();
        // 转换消息为json字符串
        String json = mapper.writeValueAsString(data);
        client.publish(TOPIC, new MqttMessage(json.getBytes(StandardCharsets.UTF_8)));
        log.info("消息发送成功，clientId:{},topic:{}", clientId, TOPIC);

    }

    public static void main(String[] args) {
    }
}
