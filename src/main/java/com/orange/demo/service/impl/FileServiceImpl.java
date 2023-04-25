package com.orange.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.demo.entity.DataHelper;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.entity.SpiSnData;
import com.orange.demo.entity.ViInfo;
import com.orange.demo.service.FileService;
import com.orange.demo.utils.FileUtils;
import com.orange.demo.utils.JsonUtils;
import com.orange.demo.utils.MqttUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public void gatherFileForSpi(File file) {
        //获取输入框的值
        Map<String, Object> map = DataHelper.getMap();
        String storePath = (String) map.get("storePath");
        String fileName = (String) map.get("fileName");
        String equType = (String) map.get("equType");
        String equName = (String) map.get("equName");
        //设备名字
        String eName =null;
        if(!StringUtils.isEmpty(equName)){
            eName = equName.toLowerCase().replace("_","-");
        }else {
            eName = file.getParentFile().getName().toLowerCase().replace("_","-");
        }
        List<EquDetailsInfo> equDetailsInfos = new ArrayList<>();
        String filePath = "";
        String filePath2 = file.getAbsolutePath() + "\\"+"insp_board.txt";
        if(fileName.contains(".txt")){
            filePath = file.getAbsolutePath()+"\\"+fileName;
        }else{
            filePath = file.getAbsolutePath() + "\\" + fileName + ".txt";
        }
        File aFile = new File(filePath);
        File bFile = new File(filePath2);
        InputStream inputStream = null;
        InputStream nInputStream = null;
        try {
            inputStream = new FileInputStream(aFile);
            nInputStream = new FileInputStream(bFile);
            equDetailsInfos = fileUtils.readTxtForSpi(inputStream, equType,filePath);
            SpiSnData spiSnData = fileUtils.readTxtForSpiSn(nInputStream, filePath2);
            if(spiSnData == null || equDetailsInfos == null || equDetailsInfos.size() <= 0){
                log.info("采集文件:{},采集不到数据！",file.getAbsolutePath());
                return;
            }
            //发送数据
            JSONObject jsonObject = JsonUtils.convertToJsonForSpi(equDetailsInfos, eName,spiSnData);
            try {
                MqttUtils.send(eName,jsonObject);
                //移动文件夹（先复制再删除）
                FileUtils.moveFolderForSpi(eName,file.getAbsolutePath(),storePath);
                log.info("文件："+ file.getAbsolutePath()+" 采集完成！");
            } catch (JsonProcessingException e) {
                log.info("MQTT转换JSON异常，原因："+ e.toString());
            } catch (MqttException e) {
                log.info("MQTT发送消息异常，原因："+ e.toString());
            } catch (Exception e){
                log.info("采集文件:"+file.getAbsolutePath()+"出现异常！原因："+e.toString());
            }
        } catch (FileNotFoundException e) {
            log.info("文件不存在！原因："+e.toString());
        }catch (Exception e){
            log.info("采集文件:"+file.getAbsolutePath()+"出现异常！原因："+e.toString());
        }
    }

    @Override
    public void gatherFileForVi(File file) {
        //获取输入框的值
        Map<String, Object> map = DataHelper.getMap();
        String storePath = (String) map.get("storePath");
        String equType = (String) map.get("equType");
        String equName = (String) map.get("equName");

        List<ViInfo> viInfos = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            viInfos = fileUtils.readTxtForVi(inputStream,equType,file);
            if(viInfos.size() <= 0){
                return;
            }
            try {
                JSONObject jsonObject = JsonUtils.convertToJsonForVi(viInfos, equName);
                MqttUtils.send(equName,jsonObject);
                //移动文件夹（先复制再删除）
                FileUtils.moveFolderForVi(file,storePath);
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
