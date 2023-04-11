package com.orange.demo.listener;

import com.orange.demo.entity.DataHelper;
import com.orange.demo.service.FileService;
import com.orange.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;
import java.util.*;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/3 15:27
 * @description: 文件监听器，监听文件改动
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    //监听文件夹
    @Override
    public void onDirectoryCreate(File directory) {
        String equType = (String) DataHelper.getMap().get("equType");
        if("SPI".equals(equType)){
            gatherFileForSpi(directory);
        }
    }

    //监听文件
    @Override
    public void onFileCreate(File file) {
        String equType = (String) DataHelper.getMap().get("equType");
        if("VI".equals(equType)){
            gatherFileForVi(file);
        }
    }

    public void gatherFileForVi(File file){
        //获取实例
        FileService fileService = SpringJobBeanFactory.getBean(FileService.class);
        if(fileService == null){
            log.info("文件夹:{}，采集出错，原因:fileService为空！",file.getAbsolutePath());
            return;
        }
        fileService.gatherFileForVi(file);
    }

    public void gatherFileForSpi(File file){
        //判断是否是需要采集的文件夹
        boolean flag = judgeGatherContentsForSpi(file);
        if(flag){
            //获取实例
            FileService fileService = SpringJobBeanFactory.getBean(FileService.class);
            if(fileService == null){
                log.info("文件夹:{}，采集出错，原因:fileService为空！",file.getAbsolutePath());
                return;
            }
            //采集文件
            fileService.gatherFileForSpi(file);
        }
    }

    //判断文件夹是不是需要采集的文件夹
    public boolean judgeGatherContentsForSpi(File directory){
        String fileName = (String) DataHelper.getMap().get("fileName");
        if(!fileName.contains(".txt")){
            fileName = fileName + ".txt";
        }
        //需要采集的文件夹里面都是txt文件，没有文件夹，以此作为判断
        File[] childrenFiles = directory.listFiles();
        //如果为空就不是
        if(Objects.isNull(childrenFiles) || childrenFiles.length == 0){
            return false;
        }
        //包含要采集的文件则为true
        for(File file : childrenFiles){
            if(file.isFile() && file.getName().equals(fileName)){
                return true;
            }
        }
        //如果没有文件夹，视为true
        List<File> files = FileUtils.getFile(directory.getAbsolutePath());
        if(files.size() <= 0){
            return true;
        }
        return false;
    }
}
