package com.orange.demo.listener;

import com.alibaba.fastjson.JSONObject;
import com.orange.demo.entity.DataHelper;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
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
        if(parentOrChild(directory)){
            createListen(directory.getAbsolutePath());
        }else{
            //获取实例
            FileUtils fileUtils = SpringJobBeanFactory.getBean(FileUtils.class);
            if(fileUtils == null){
                log.info("文件夹:{},采集出错,原因:fileUtils为空！",directory.getAbsolutePath());
                return;
            }
            //获取输入框的值
            Map<String, Object> map = DataHelper.getMap();
            String storePath = (String) map.get("storePath");
            String fileName = (String) map.get("fileName");
            String equType = (String) map.get("equType");
            //设备名字
            String eName = directory.getParentFile().getName();
            List<EquDetailsInfo> list = new ArrayList<>();
            String filePath = "";
            if(fileName.contains(".txt")){
                filePath = directory.getAbsolutePath()+"\\"+fileName;
            }else{
                filePath = directory.getAbsolutePath() + "\\" + fileName + ".txt";
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
                MqttUtils.send(eName,jsonObject);
                //移动文件夹（先复制再删除）
                FileUtils.moveFolder(eName,directory.getAbsolutePath(),storePath);
            } catch (FileNotFoundException exception) {
                log.info("文件不存在！");
            }
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
    public void createListen(String path){
        List<File> files = FileUtils.getFile(path);
        if(files.size() <= 0){
            log.info("文件夹："+path+"下，没有文件夹，不创建监控！");
            return;
        }
        for(File file : files){
            try {
                FileMonitor fileMonitor = new FileMonitor(1000);
                fileMonitor.monitor(file.getAbsolutePath(),new FileListener());
                fileMonitor.start();
                log.info("文件夹：" + file.getAbsolutePath() + "，创建监听成功!");
            } catch (Exception e) {
                log.info("文件夹：" + file.getAbsolutePath() + "，创建监听失败,跳过!原因：" + e.toString());
            }
        }
    }
}
