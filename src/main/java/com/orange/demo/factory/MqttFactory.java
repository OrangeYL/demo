package com.orange.demo.factory;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/8 14:12
 * @description:
 */
@Slf4j
public class MqttFactory {

    //MQTT地址
    private final static String HOST = "tcp://10.219.235.51:1883";

    private static MqttClient client;

    /**
     *   获取客户端实例
     *   单例模式, 存在则返回, 不存在则初始化
     */
    public static MqttClient getInstance(String clientId) {
        if (client == null) {
            init(clientId);
        }
        return client;
    }

    /**
     *   初始化客户端
     */
    public static void init(String clientId) {
        try {
            client = new MqttClient(HOST, clientId,new MemoryPersistence());
            // MQTT配置对象
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(clientId);
            options.setPassword(clientId.toCharArray());
            options.setAutomaticReconnect(true);
            if (!client.isConnected()) {
                client.connect(options);
            }
        } catch (MqttException e) {
            log.error("连接MQTT服务器--异常,clientId:{},exception:{}", clientId, e.toString());
        }
    }
}