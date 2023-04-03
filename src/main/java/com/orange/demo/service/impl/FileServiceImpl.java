package com.orange.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.demo.entity.DataHelper;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.service.FileService;
import com.orange.demo.utils.FileUtils;
import com.orange.demo.utils.JsonUtils;
import com.orange.demo.utils.MqttUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: Li ZhiCheng
 * @create: 2023-04-2023/4/3 11:38
 * @description:
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    FileUtils fileUtils;

    @Override
    public void gatherFile(File file) {
        //获取输入框的值
        Map<String, Object> map = DataHelper.getMap();
        String storePath = (String) map.get("storePath");
        String fileName = (String) map.get("fileName");
        String equType = (String) map.get("equType");

        //设备名字
        String eName = file.getParentFile().getName();
        List<EquDetailsInfo> equDetailsInfos = new ArrayList<>();
        String filePath = "";
        if(fileName.contains(".txt")){
            filePath = file.getAbsolutePath()+"\\"+fileName;
        }else{
            filePath = file.getAbsolutePath() + "\\" + fileName + ".txt";
        }
        File aFile = new File(filePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(aFile);
            equDetailsInfos = fileUtils.readTxt(inputStream, equType,filePath);
            if(equDetailsInfos.size() <= 0){
                return;
            }
            //发送数据
            JSONObject jsonObject = JsonUtils.convertToJson(equDetailsInfos, eName);
            try {
                MqttUtils.send(eName,jsonObject);
                //移动文件夹（先复制再删除）
                FileUtils.moveFolder(eName,file.getAbsolutePath(),storePath);
                log.info("文件："+ file.getAbsolutePath()+" 采集完成！");
            } catch (JsonProcessingException e) {
                log.info("MQTT转换JSON异常，原因："+ e.toString());
            } catch (MqttException e) {
                log.info("MQTT发送消息异常，原因："+ e.toString());
            }
        } catch (FileNotFoundException e) {
            log.info("文件不存在！原因："+e.toString());
        }
    }
}
