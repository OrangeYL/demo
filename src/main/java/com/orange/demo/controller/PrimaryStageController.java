package com.orange.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.orange.demo.entity.DataHelper;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.listener.FileListener;
import com.orange.demo.utils.*;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/23 15:02
 * @description:
 */
@FXMLController
@Slf4j
public class PrimaryStageController implements Initializable {
    @FXML
    private TextField pathField;
    @FXML
    private TextField storeField;
    @FXML
    private TextField fileNameField;
    @FXML
    private ComboBox<String> equTypeBox;
    @FXML
    private Button sureBt;
    @FXML
    private Button scanBt;
    @Autowired
    private FileUtils fileUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("path"))){
            pathField.setText(PropertiesUtil.getValue("path"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("storePath"))){
            storeField.setText(PropertiesUtil.getValue("storePath"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("fileName"))){
            fileNameField.setText(PropertiesUtil.getValue("fileName"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("equType"))){
            equTypeBox.setValue(PropertiesUtil.getValue("equType"));
        }
        //触发创建监听按钮
        sureBt.setOnAction(e -> {
            //得到输入框的值
            String path = pathField.getText().trim();
            String storePath = storeField.getText().trim();
            String fileName = fileNameField.getText().trim();
            String equType = equTypeBox.getValue();
            //保存在配置文件中
            PropertiesUtil.setValue("path",path);
            PropertiesUtil.setValue("storePath",storePath);
            PropertiesUtil.setValue("fileName",fileName);
            PropertiesUtil.setValue("equType",equType);

            //存进Map里面，后续需要使用
            DataHelper.getMap().put("storePath",storePath);
            DataHelper.getMap().put("fileName",fileName);
            DataHelper.getMap().put("equType", equType);

            //得到所有的设备文件夹
            List<File> file = FileUtils.getFile(path);
            if(file.size() <= 0){
                log.info("监控路径："+path+"下，没有设备文件夹");
                return;
            }
            //遍历设备文件夹并创建监听
            file.forEach(item -> {
                FileMonitor fileMonitor = new FileMonitor(1000);
                fileMonitor.monitor(item.getAbsolutePath(), new FileListener());
                try {
                    fileMonitor.start();
                    log.info("文件夹："+item.getName()+"，创建监听成功!");
                } catch (Exception exception) {
                    log.info("文件夹："+item.getName()+"，创建监听失败!原因："+exception.toString());
                    exception.printStackTrace();
                }
            });
            //增加窗口提示
            Alert alert = null;
            try {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.titleProperty().set("提示");
                alert.contentTextProperty().set("创建成功！");
                alert.showAndWait();
            }catch (Exception exception) {
                exception.printStackTrace();
            }finally {
                if(null != alert){
                    alert.close();
                }
            }
        });
        //触发扫描一次按钮
        scanBt.setOnAction(e ->{
            //得到输入框的值
            String path = pathField.getText().trim();
            String storePath = storeField.getText().trim();
            String fileName = fileNameField.getText().trim();
            String equType = equTypeBox.getValue();

            //保存在配置文件中
            PropertiesUtil.setValue("path",path);
            PropertiesUtil.setValue("storePath",storePath);
            PropertiesUtil.setValue("fileName",fileName);
            PropertiesUtil.setValue("equType",equType);

            //得到所有的设备文件夹
            List<File> files = FileUtils.getFile(path);
            if(files .size() <= 0){
                log.info("路径："+path+"下，没有设备文件夹!");
                return;
            }
            files.forEach(item ->{
                //获取设备文件夹的子文件夹
                List<File> list = FileUtils.getFile(item.getAbsolutePath());
                if(list.size() <= 0){
                    return;
                }
                //设备名称
                String eName = item.getName();
                list.forEach(data ->{
                    List<EquDetailsInfo> equDetailsInfos = new ArrayList<>();
                    String filePath = "";
                    if(fileName.contains(".txt")){
                        filePath = data.getAbsolutePath()+"\\"+fileName;
                    }else{
                        filePath = data.getAbsolutePath() + "\\" + fileName + ".txt";
                    }
                    File file = new File(filePath);
                    InputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(file);
                        equDetailsInfos = fileUtils.readTxt(inputStream, equType);
                        //移动文件夹（先复制再删除）
                        FileUtils.moveFolder(eName,data.getAbsolutePath(),storePath);
                        JSONObject jsonObject = JsonUtils.convertToJson(equDetailsInfos, eName);
                        MqttUtils.send(eName,jsonObject);
                    } catch (FileNotFoundException exception) {
                        log.info("文件不存在！原因："+e.toString());
                    }
                });
                log.info("扫描结束，路径:{}",path);
            });
            //增加窗口提示
            Alert alert =null;
            try {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.titleProperty().set("提示");
                alert.headerTextProperty().set("提示");
                alert.contentTextProperty().set("扫描结束！");
                alert.showAndWait();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                if(null != alert){
                    alert.close();
                }
            }
        });
    }
}
