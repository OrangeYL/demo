package com.orange.demo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.orange.demo.entity.EquDetailsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/3 15:33
 * @description: 文件工具类
 */
@Slf4j
@Component
public class FileUtils {

    @Value(value = "${elmUrl}")
    private String elmUrl;

    public List<EquDetailsInfo> readTxt(InputStream is, String equType){
        //调用ELM接口得到对应的padNo
        List<NameValuePair> list = new LinkedList<>();
        List<String> padNos = new ArrayList<>();

        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        //设备详细数据
        List<EquDetailsInfo> equDetailsInfos = null;
        try {
            reader = new InputStreamReader(is, "GBK");
            bufferedReader = new BufferedReader(reader);
            int index = 1;
            equDetailsInfos = new ArrayList<>();
            //第一行只取机型
            String line = bufferedReader.readLine();
            //机型
            String machineType = null;
            if(line != null){
                String[] strings = line.split(",");
                String s = strings[0];
                String[] strs = s.split("\\|");
                machineType = strs[strs.length-1];
            }
            //根据设备类型与机型调用ELM接口得到padNo
            BasicNameValuePair pair1 = new BasicNameValuePair("equType",equType);
            BasicNameValuePair pair2 = new BasicNameValuePair("machineType", machineType);
            list.add(pair1);
            list.add(pair2);
            String result = HttpUtil.doGetJson(elmUrl, list);
            if(StringUtils.isNotBlank(result)){
                JSONObject json = JSONObject.parseObject(result);
                if("true".equals(json.getString("success"))){
                    JSONArray array = json.getJSONArray("result");
                    if(CollectionUtils.isNotEmpty(array)){
                        for(Object jsonObject : array){
                            JSONObject data = (JSONObject) jsonObject;
                            String padNo = data.getString("padNo");
                            if(StringUtils.isNotBlank(padNo)){
                                padNos.add(padNo);
                            }
                        }
                    }
                }
            }
            while((line = bufferedReader.readLine()) != null){
                //第二行列名跳过
                if(index == 1){
                    index++;
                    continue;
                }
                //设备数据，即txt文件中的数据
                EquDetailsInfo equDetailsInfo = new EquDetailsInfo();
                String[] data = line.split(",");
                //跟ELM接口得到的数据进行比较，得到需要读取的行
                String padNo = data[3];
                for(int i = 0;i < padNos.size();i++){
                    if(padNo.equals(padNos.get(i))){
                        equDetailsInfo.setMachineType(machineType);
                        equDetailsInfo.setBoardId(data[0]);
                        equDetailsInfo.setPadNo(data[3]);
                        equDetailsInfo.setInspStTime(data[8]);
                        equDetailsInfo.setInspVol(Double.valueOf(data[9]));
                        equDetailsInfo.setInspArea(Double.valueOf(data[10]));
                        equDetailsInfo.setInspHei(Double.valueOf(data[11]));
                        equDetailsInfo.setInspX(Double.valueOf(data[12]));
                        equDetailsInfo.setInspY(Double.valueOf(data[13]));
                        equDetailsInfos.add(equDetailsInfo);
                        break;
                    }
                }
            }
            bufferedReader.close();
            reader.close();
            is.close();
        } catch (Exception e) {
            log.info("文件读取错误！");
        }
        return equDetailsInfos;
    }
    /**
     *获取文件夹
     **/
    public static List<File> getFile(String path){
        if(path == null || path.equals("")){
            return new ArrayList<>();
        }
        return getAllFile(new File(path));
    }
    public static List<File> getAllFile(File dirFile){
        // 如果文件夹不存在或者不是文件夹，则返回空链表
        if(Objects.isNull(dirFile) || !dirFile.exists() || dirFile.isFile()){
            return new ArrayList<>();
        }
        File[] childrenFiles =  dirFile.listFiles();
        if(Objects.isNull(childrenFiles) || childrenFiles.length == 0){
            return new ArrayList<>();
        }
        List<File> fileNames = new ArrayList<>();
        for(File childFile : childrenFiles) {
            if(!childFile.isFile()){
                fileNames.add(childFile);
            }
        }
        return fileNames;
    }

    // 删除某个目录及目录下的所有子目录和文件
    public static boolean deleteDir(File dir) {
        // 如果是文件夹
        if (dir.isDirectory()) {
            // 则读出该文件夹下的的所有文件
            String[] children = dir.list();
            if (children != null) {
                // 递归删除目录中的子目录下
                for (int i = 0; i < children.length; i++) {
                    // File f=new File（String parent ，String child）
                    // parent抽象路径名用于表示目录，child 路径名字符串用于表示目录或文件。
                    // 连起来刚好是文件路径
                    boolean isDelete = deleteDir(new File(dir, children[i]));
                    // 如果删完了，没东西删，isDelete==false的时候，则跳出此时递归
                    if (!isDelete) {
                        return false;
                    }
                }
            }
        }
        // 读到的是一个文件或者是一个空目录，则可以直接删除
        return dir.delete();
    }
    /**
     复制某个目录及目录下的所有子目录和文件到新文件夹
     eName：设备名称
     oldPath：旧路径
     newPath：新路径
     **/
    public static void copyFolder(String eName,String oldPath, String newPath) {
        try {
            //创建设备文件夹
            String ePath = newPath + "\\" + eName;
            File eFile = new File(ePath);
            eFile.mkdirs();

            //得到旧文件夹的名字
            String name = new File(oldPath).getName();
            String pathNew = eFile.getAbsolutePath() + "\\"+name;
            //创建设备文件夹下的文件夹
            File pathFile = new File(pathNew);
            pathFile.mkdirs();

            // 读取整个文件夹的内容到file字符串数组，下面设置一个游标i，不停地向下移开始读这个数组
            File fileList = new File(oldPath);
            String[] file = fileList.list();
            if(file == null || file.length <= 0){
                return;
            }
            // 要注意，这个temp仅仅是一个临时文件指针
            // 整个程序并没有创建临时文件
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                // 如果oldPath以路径分隔符/或者\结尾，那么则oldPath/文件名就可以了
                // 否则要自己oldPath后面补个路径分隔符再加文件名
                // 谁知道你传递过来的参数是f:/a还是f:/a/啊？
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                // 如果游标遇到文件
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    // 复制并且改名
                    FileOutputStream output = new FileOutputStream(pathNew
                            + "/" + (temp.getName()).toString());
                    byte[] bufferArray = new byte[1024 * 64];
                    int prereadlength;
                    while ((prereadlength = input.read(bufferArray)) != -1) {
                        output.write(bufferArray, 0, prereadlength);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                // 如果游标遇到文件夹
                if (temp.isDirectory()) {
                    copyFolder(eName,oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            log.info("复制整个文件夹内容操作出错");
        }
    }
    public static void moveFolder(String eName,String oldPath, String newPath) {
        // 先复制文件
        copyFolder(eName,oldPath, newPath);
        // 则删除源文件，以免复制的时候错乱
        deleteDir(new File(oldPath));
    }
}
