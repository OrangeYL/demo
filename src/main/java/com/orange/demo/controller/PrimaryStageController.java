package com.orange.demo.controller;

import com.orange.demo.entity.DataHelper;
import com.orange.demo.listener.FileListener;
import com.orange.demo.service.FileService;
import com.orange.demo.utils.*;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


import java.io.File;
import java.net.URL;
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
    private TextField equNameField;
    @FXML
    private ComboBox<String> equTypeBox;
    @FXML
    private Button sureBt;
    @Autowired
    private FileService fileService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //检查配置文件是否有值，初始化输入框
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("path"))){
            pathField.setText(PropertiesUtil.getValue("path"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("storePath"))){
            storeField.setText(PropertiesUtil.getValue("storePath"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("fileName"))){
            fileNameField.setText(PropertiesUtil.getValue("fileName"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("equName"))){
            equNameField.setText(PropertiesUtil.getValue("equName"));
        }
        if(!StringUtils.isEmpty(PropertiesUtil.getValue("equType"))){
            equTypeBox.setValue(PropertiesUtil.getValue("equType"));
        }
    }
    //触发创建监听按钮
    @FXML
    public void sure(ActionEvent actionEvent) {
        //得到输入框的值
        String path = pathField.getText().trim();
        String storePath = storeField.getText().trim();
        String fileName = fileNameField.getText().trim();
        String equName = equNameField.getText().trim();
        String equType = equTypeBox.getValue();
        if("VI".equals(equType)){
            if(StringUtils.isEmpty(path) || StringUtils.isEmpty(storePath)  || StringUtils.isEmpty(equType) || StringUtils.isEmpty(equName)){
                log.info("path:{},storePath:{},equType:{},equName:{}",path,storePath,equType,equName);
                popTip("警告","监控配置信息填写不全，请确保文件路径，移动路径，设备名称，设备类型已填!");
                return;
            }
        }else if("SPI".equals(equType)){
            if(StringUtils.isEmpty(path) || StringUtils.isEmpty(storePath) || StringUtils.isEmpty(fileName) || StringUtils.isEmpty(equType)){
                log.info("path:{},storePath:{},fileName:{},equType:{}",path,storePath,fileName,equType);
                popTip("警告","监控配置信息填写不全，请确保文件路径，移动路径，文件名称，设备类型已填!");
                return;
            }
        }
        //保存数据
        saveDataToPropertiesAndMap(path,storePath,fileName,equType,equName);
        //为path路径下设置监控
        try {
            createListener(path);
            //禁用按钮
            sureBt.setDisable(true);
            log.info("文件夹：" + path + "，创建监听成功!");
            //增加窗口提示
            popTip("提示","创建成功!");
        } catch (Exception e) {
            log.info("文件夹：" + path + "，创建监听失败!原因：" + e.toString());
        }
    }
    //扫描一次
    @FXML
    public void scan(ActionEvent actionEvent) throws Exception {
        //得到输入框的值
        String path = pathField.getText().trim();
        String storePath = storeField.getText().trim();
        String fileName = fileNameField.getText().trim();
        String equName = equNameField.getText().trim();
        String equType = equTypeBox.getValue();
        if("VI".equals(equType)){
            if(StringUtils.isEmpty(path) || StringUtils.isEmpty(storePath)  || StringUtils.isEmpty(equType) || StringUtils.isEmpty(equName)){
                log.info("path:{},storePath:{},equType:{},equName:{}",path,storePath,equType,equName);
                popTip("警告","监控配置信息填写不全，请确保文件路径，移动路径，设备名称，设备类型已填!");
                return;
            }
        }else if("SPI".equals(equType)){
            if(StringUtils.isEmpty(path) || StringUtils.isEmpty(storePath) || StringUtils.isEmpty(fileName) || StringUtils.isEmpty(equType)){
                log.info("path:{},storePath:{},fileName:{},equType:{}",path,storePath,fileName,equType);
                popTip("警告","监控配置信息填写不全，请确保文件路径，移动路径，文件名称，设备类型已填!");
                return;
            }
        }
        //保存在配置文件中
        saveDataToPropertiesAndMap(path, storePath, fileName, equType,equName);
        //开始扫描
        if("SPI".equals(equType)){
            scanForSpi(path);
        }else if("VI".equals(equType)){
            scanForVi(path);
        }
        //增加窗口提示
        popTip("提示","扫描结束!");
    }
    //SPI扫描
    public void scanForSpi(String path){
        //得到所有的path路径下文件夹
        List<File> files = FileUtils.getFile(path);
        if(files .size() <= 0){
            log.info("路径："+path+"下，没有文件夹!");
            return;
        }
        for(File file : files){
            //设备文件夹
            List<File> fileList = FileUtils.getFile(file.getAbsolutePath());
            if(fileList.size() <= 0){
                log.info("路径："+file.getAbsolutePath()+"下，没有文件夹，跳过该文件!");
                continue;
            }
            for(File item : fileList){
                //采集的文件夹
                List<File> list = FileUtils.getFile(item.getAbsolutePath());
                if(list.size() <= 0){
                    log.info("设备路径："+item.getAbsolutePath()+"下，没有文件夹，跳过该文件!");
                }
                for(File data : list){
                    fileService.gatherFileForSpi(data);
                }
            }
        }
    }
    //VI扫描
    public void scanForVi(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        if(files == null || files.length == 0){
            log.info("在路径:"+path+"下，没有文件！");
            return;
        }
        for(File f : files){
            fileService.gatherFileForVi(f);
        }
    }
    //保存数据到配置文件和Map中
    public void saveDataToPropertiesAndMap(String path,String storePath,String fileName,String equType,String equName){
        //保存到配置文件中
        PropertiesUtil.setValue("path",path);
        PropertiesUtil.setValue("storePath",storePath);
        PropertiesUtil.setValue("fileName",fileName);
        PropertiesUtil.setValue("equName",equName);
        PropertiesUtil.setValue("equType",equType);
        //保存到map中，后续需要使用
        DataHelper.getMap().put("path",path);
        DataHelper.getMap().put("storePath",storePath);
        DataHelper.getMap().put("fileName",fileName);
        String s = equName.replace("_","-");
        DataHelper.getMap().put("equName",s.toLowerCase());
        DataHelper.getMap().put("equType",equType);
    }
    //创建监听
    public void createListener(String path) throws Exception {
        FileMonitor fileMonitor = new FileMonitor(1000);
        fileMonitor.monitor(path, new FileListener());
        fileMonitor.start();
    }
    //弹出提示
    public void popTip(String title,String text){
        Alert alert = null;
        try {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.titleProperty().set(title);
            alert.contentTextProperty().set(text);
            alert.showAndWait();
        } catch (Exception e) {
            log.info("弹出警告错误：" + e.toString());
        }finally {
            if(null != alert){
                alert.close();
            }
        }
    }
}
