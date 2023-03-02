package com.orange.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/2 10:11
 * @description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test{

    @FXML
    private TextField pathField;
    @FXML
    private TextField storeField;
    @FXML
    private TextField fileNameField;
    @FXML
    private Button sureBt;

    //测试出发按钮获取值
    @org.junit.Test
    public void testGet(){
        sureBt.setOnAction(e->{
            //文件路径
            String path = pathField.getText();
            //存放路径
            String storePath = storeField.getText();
            //文件名称
            String fileName = fileNameField.getText();

            System.out.println("文件路径：" + path);
            System.out.println("存放路径：" + storePath);
            System.out.println("文件名称：" + fileName);
        });
    }
}
