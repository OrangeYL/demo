package com.orange.demo.factory;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/8 14:12
 * @description: Mqtt工厂
 */
@Slf4j
public class MqttFactory {

    //MQTT地址
    private final static String HOST = "tcp://10.219.235.51:1883";
    //用户名
    private final static String USERNAME = "admin";
    //密码
    private final static String PASSWORD = "public";

    private static Map<String,MqttClient> map = new HashMap<>();
    /**
     *   获取客户端实例
     */
    public static MqttClient getInstance(String clientId) {
        MqttClient c =  map.get(clientId);
        if(c != null){
            return c;
        }
        MqttClient client = init(clientId);
        map.put(clientId,client);
        return client;
    }

    /**
     *   初始化客户端
     */
    public static MqttClient init(String clientId) {
        MqttClient client = null;
        try {
            client = new MqttClient(HOST,clientId,new MemoryPersistence());
            // MQTT配置对象
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            options.setAutomaticReconnect(true);
            if (!client.isConnected()) {
                client.connect(options);
            }
        } catch (MqttException e) {
            log.info("连接MQTT服务器--异常,clientId:{},exception:{}", clientId, e.toString());
        }
        return client;
    }
}
