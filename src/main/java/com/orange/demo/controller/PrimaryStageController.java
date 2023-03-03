package com.orange.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.orange.demo.listener.FileListener;
import com.orange.demo.service.EquDetailsInfoService;
import com.orange.demo.service.EquInfoService;
import com.orange.demo.utils.FileMonitor;
import com.orange.demo.utils.FileUtils;
import com.orange.demo.utils.PropertiesUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;


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
public class PrimaryStageController implements Initializable {
    @FXML
    private TextField pathField;
    @FXML
    private TextField storeField;
    @FXML
    private TextField fileNameField;
    @FXML
    private Button sureBt;

    @Autowired
    private EquInfoService equInfoService;
    @Autowired
    private EquDetailsInfoService equDetailsInfoService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //判断配置文件有没有值，如果有就显示出来
        String path = PropertiesUtil.getValue("path");
        String storePath = PropertiesUtil.getValue("storePath");
        String fileName = PropertiesUtil.getValue("fileName");
        if (StringUtils.isNotBlank(path)) {
            pathField.setText(path);
        }
        if (StringUtils.isNotBlank(storePath)) {
            storeField.setText(storePath);
        }
        if (StringUtils.isNotBlank(fileName)) {
            fileNameField.setText(fileName);
        }
        sureBt.setOnAction(e -> {
            String realPath = pathField.getText();
            String realStorePath = storeField.getText();
            String realFileName = fileNameField.getText();

            //写进配置文件
            PropertiesUtil.setValue("path", realPath);
            PropertiesUtil.setValue("storePath", realStorePath);
            PropertiesUtil.setValue("fileName", realFileName);

            //得到所有的设备文件夹
            List<File> file = FileUtils.getFile(realPath);
            //遍历设备文件夹并创建监听
            file.forEach(item -> {
                FileMonitor fileMonitor = new FileMonitor(1000);
                fileMonitor.monitor(item.getAbsolutePath(), new FileListener());
                try {
                    fileMonitor.start();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

        });
    }
}
