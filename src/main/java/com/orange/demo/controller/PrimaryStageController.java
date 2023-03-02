package com.orange.demo.controller;

import com.orange.demo.job.FileJob;
import com.orange.demo.service.EquDetailsInfoService;
import com.orange.demo.service.EquInfoService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.net.URL;
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
        sureBt.setOnAction(e->{
            //文件路径
            String path = pathField.getText();
            //存放路径
            String storePath = storeField.getText();
            //文件名称
            String fileName = fileNameField.getText();
            try {
                //获取任务调度器的实例
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                //定义任务调度实例，并与Job(具体任务)绑定
                JobDetail job = JobBuilder.newJob(FileJob.class)
                        .withIdentity("FileJob", "FileJobGroup")
                        .build();
                job.getJobDataMap().put("equInfoService",equInfoService);
                job.getJobDataMap().put("equDetailsInfoService",equDetailsInfoService);
                //定义触发器，会马上执行一次，接着每隔10分钟执行一次
                Trigger trigger= TriggerBuilder.newTrigger()
                        .withIdentity("testTrigger", "testTriggerGroup")
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(2))
                        .usingJobData("path",path)
                        .usingJobData("storePath",storePath)
                        .usingJobData("fileName",fileName)
                        .build();
                //使用调度器调度任务的执行
                scheduler.scheduleJob(job,trigger);
                //开启任务
                scheduler.start();
            } catch (SchedulerException exception) {
                exception.printStackTrace();
            }
        });
    }
}
