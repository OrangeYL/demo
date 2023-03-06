package com.orange.demo.controller;

import com.orange.demo.entity.DataHelper;
import com.orange.demo.listener.FileListener;
import com.orange.demo.service.EquDetailsInfoService;
import com.orange.demo.service.EquInfoService;
import com.orange.demo.utils.FileMonitor;
import com.orange.demo.utils.FileUtils;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sureBt.setOnAction(e -> {
            //得到输入框的值
            String path = pathField.getText();
            String storePath = storeField.getText();
            String fileName = fileNameField.getText();

            //存进Map里面，后续需要使用
            DataHelper.getMap().put("storePath",storePath);
            DataHelper.getMap().put("fileName",fileName);
            //得到所有的设备文件夹
            List<File> file = FileUtils.getFile(path);
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
