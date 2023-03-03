package com.orange.demo.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.entity.EquInfo;
import com.orange.demo.service.EquDetailsInfoService;
import com.orange.demo.service.EquInfoService;
import com.orange.demo.utils.FileUtils;
import com.orange.demo.utils.PropertiesUtil;
import com.orange.demo.utils.SpringJobBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/3 15:27
 * @description: 文件监听器，监听文件改动
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    @Override
    public void onDirectoryCreate(File directory) {
        EquInfoService equInfoService = SpringJobBeanFactory.getBean(EquInfoService.class);
        EquDetailsInfoService equDetailsInfoService = SpringJobBeanFactory.getBean(EquDetailsInfoService.class);
        //文件名
        String fileName = PropertiesUtil.getValue("fileName");
        String storePath = PropertiesUtil.getValue("storePath");
        //设备名字
        String eName = directory.getParentFile().getName();
        EquInfo equInfo = new EquInfo();
        equInfo.setEName(eName);
        equInfo.setCreateTime(new Date());

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
            equInfo = FileUtils.readTxt(inputStream,equInfo);
            //移动文件夹（先复制再删除）
            FileUtils.moveFolder(eName,directory.getAbsolutePath(),storePath);
        } catch (FileNotFoundException exception) {
            log.info("文件不存在！");
        }
        //保存到数据库
        LambdaQueryWrapper<EquInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquInfo::getEName, eName);
        EquInfo equInfo1 = equInfoService.getOne(wrapper);
        List<EquDetailsInfo> infos = equInfo.getList();
        if (equInfo1 == null) {
            equInfoService.save(equInfo);
            if (infos.size() > 0) {
                for (EquDetailsInfo info : infos) {
                    info.setEId(equInfo.getId());
                    info.setCreateTime(new Date());
                    equDetailsInfoService.save(info);
                }
            }
        } else {
            if (infos.size() > 0) {
                for (EquDetailsInfo info : infos) {
                    info.setEId(equInfo1.getId());
                    info.setCreateTime(new Date());
                    equDetailsInfoService.save(info);
                }
            }
        }
    }
}
