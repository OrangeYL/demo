package com.orange.demo.listener;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.demo.entity.DataHelper;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/3 15:27
 * @description: 文件监听器，监听文件改动
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    @Override
    public void onDirectoryCreate(File directory) {
        //如果是监控路径下的下一层目录
        if(parentOrChild(directory)){
            //得到设备文件夹
            List<File> fileList = FileUtils.getFile(directory.getAbsolutePath());
            if(fileList.size() > 0) {
                for (File file : fileList) {
                    List<File> files = FileUtils.getFile(file.getAbsolutePath());
                    if (files.size() > 0) {
                        for (File item : files) {
                            gatherFile(item);
                        }
                    }
                }
            }
        }else if(judgeContents(directory)){ //如果是监控路径下的下下层目录,即设备文件夹
            List<File> files = FileUtils.getFile(directory.getAbsolutePath());
            if(files.size() > 0){
                for(File item : files){
                    gatherFile(item);
                }
            }
        }else{ //要采集的文件夹
            gatherFile(directory);
        }
    }
    public void gatherFile(File item){
        //获取实例
        FileUtils fileUtils = SpringJobBeanFactory.getBean(FileUtils.class);
        if(fileUtils == null){
            log.info("文件夹:{}，采集出错，原因:fileUtils为空！",item.getAbsolutePath());
            return;
        }
        //获取输入框的值
        Map<String, Object> map = DataHelper.getMap();
        String storePath = (String) map.get("storePath");
        String fileName = (String) map.get("fileName");
        String equType = (String) map.get("equType");

        //设备名字
        String eName = item.getParentFile().getName();
        List<EquDetailsInfo> list = new ArrayList<>();
        String filePath = "";
        if(fileName.contains(".txt")){
            filePath = item.getAbsolutePath()+"\\"+fileName;
        }else{
            filePath = item.getAbsolutePath() + "\\" + fileName + ".txt";
        }
        File file = new File(filePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            list = fileUtils.readTxt(inputStream,equType,filePath);
            if(list.size() <= 0){
                return;
            }
            //发送消息存进时序库
            JSONObject jsonObject = JsonUtils.convertToJson(list,eName);
            try {
                MqttUtils.send(eName,jsonObject);
                //移动文件夹（先复制再删除）
                FileUtils.moveFolder(eName,item.getAbsolutePath(),storePath);
                log.info("文件："+filePath+"采集成功！");
            } catch (JsonProcessingException e) {
                log.info("MQTT转换JSON异常，原因："+ e.toString());
            } catch (MqttException e) {
                log.info("MQTT发送消息异常，原因："+ e.toString());
            }
        } catch (FileNotFoundException e) {
            log.info("文件不存在："+ e.toString());
        }
    }
    public boolean parentOrChild(File directory){
        //得到父级目录
        String parentPath = directory.getParentFile().getAbsolutePath();
        String path = (String) DataHelper.getMap().get("path");
        if(!StringUtils.isEmpty(path)){
            if(parentPath.equals(path)){
                return true;
            }
        }
        return false;
    }
    public boolean judgeContents(File directory){
        //上上级目录
        String parentPath = directory.getParentFile().getParentFile().getAbsolutePath();
        String path = (String) DataHelper.getMap().get("path");
        if(!StringUtils.isEmpty(path)){
            if(parentPath.equals(path)){
                return true;
            }
        }
        return false;
    }
}
