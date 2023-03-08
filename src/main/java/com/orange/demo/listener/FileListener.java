package com.orange.demo.listener;

import com.orange.demo.entity.DataHelper;
import com.orange.demo.entity.EquInfo;
import com.orange.demo.service.EquInfoService;
import com.orange.demo.utils.FileUtils;
import com.orange.demo.utils.SpringJobBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/3 15:27
 * @description: 文件监听器，监听文件改动
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    @Override
    public void onDirectoryCreate(File directory) {
        //获取service
        EquInfoService equInfoService = SpringJobBeanFactory.getBean(EquInfoService.class);
        FileUtils fileUtils = SpringJobBeanFactory.getBean(FileUtils.class);
        //获取输入框的值
        Map<String, Object> map = DataHelper.getMap();
        String storePath = (String) map.get("storePath");
        String fileName = (String) map.get("fileName");
        String equType = (String) map.get("equType");
        //设备名字
        String eName = directory.getParentFile().getName();
        EquInfo equInfo = new EquInfo();
        equInfo.setEName(eName);
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
            if("SPI".equals(equType)){
                equInfo = fileUtils.readTxt(inputStream,equInfo,equType);
            }
            //移动文件夹（先复制再删除）
            FileUtils.moveFolder(eName,directory.getAbsolutePath(),storePath);
        } catch (FileNotFoundException exception) {
            log.info("文件不存在！");
        }
        //保存到数据库
        if(equInfoService != null){
            equInfoService.saveEntity(equInfo,eName);
        }
    }
}
