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
        try {
            String equType = (String) DataHelper.getMap().get("equType");
            if("SPI".equals(equType)){
                gatherFileForSpi(directory);
            }
        } catch (Exception e) {
            log.info("采集文件:"+directory.getAbsolutePath()+"出现异常！原因："+e.toString());
        }
    }

    //监听文件
    @Override
    public void onFileCreate(File file) {
        try {
            String equType = (String) DataHelper.getMap().get("equType");
            if("VI".equals(equType)){
                gatherFileForVi(file);
            }
        } catch (Exception e) {
            log.info("采集文件"+file.getAbsolutePath()+"出现异常！原因："+e.toString());
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
        }else{
            log.info("文件:"+file.getAbsolutePath()+"不是要采集的文件！");
        }
    }

    //判断文件夹是不是需要采集的文件夹
    public boolean judgeGatherContentsForSpi(File directory){
        try {
            String fileName = (String) DataHelper.getMap().get("fileName");
            if(!fileName.contains(".txt")){
                fileName = fileName + ".txt";
            }
            //需要采集的文件夹里面都是txt文件，没有文件夹，以此作为判断
            File[] childrenFiles = directory.listFiles();
            //如果为空就不是
            if(Objects.isNull(childrenFiles) || childrenFiles.length == 0){
                log.info("文件：" +directory.getAbsolutePath()+"不是采集文件！" );
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
        } catch (Exception e) {
           log.info("判断文件:{},采集出错,原因：{}",directory.getAbsolutePath(),e.toString());
        }
        return false;
    }
}
