package com.orange.demo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.entity.SpiSnData;
import com.orange.demo.entity.SpiVO;
import com.orange.demo.entity.ViInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;

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

    public SpiSnData readTxtForSpiSn(InputStream is,String filePath){
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        SpiSnData spiSnData = null;
        try{
            reader = new InputStreamReader(is, "GBK");
            bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            if(StringUtils.isBlank(line)){
                log.info("文件：" + filePath + "读取不到数据，直接返回！");
                return new SpiSnData();
            }
            String[] strs = line.split(",");
            String sn = strs[5];
            spiSnData = new SpiSnData();
            spiSnData.setSn(sn);
        } catch (Exception e){
            log.info("文件读取错误！原因："+e.toString());
        } finally {
            try {
                if(null != bufferedReader){
                    bufferedReader.close();
                }
                if(null != reader){
                    reader.close();
                }
                if(null != is){
                    is.close();
                }
            } catch (IOException e) {
               log.info("关闭流错误,原因：{}",e.toString());
            }
        }
        return spiSnData;
    }
    public List<EquDetailsInfo> readTxtForSpi(InputStream is, String equType,String filePath){
        //调用ELM接口得到对应的padNo，arrayId
        List<NameValuePair> list = new LinkedList<>();
        List<SpiVO> padNos = new ArrayList<>();

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
            String line = null;
            line = bufferedReader.readLine();
            //机型
            if (StringUtils.isBlank(line)) {
                log.info("文件：" + filePath + "读取不到机型，直接返回！");
                return new ArrayList<>();
            }
            String[] strings = line.split(",");
            String s = strings[0];
            String[] strs = s.split("\\|");
            String machineType = strs[strs.length - 1];
            //根据设备类型与机型调用ELM接口得到padNo,arrayId
            BasicNameValuePair pair1 = new BasicNameValuePair("equType", equType);
            BasicNameValuePair pair2 = new BasicNameValuePair("machineType", machineType);
            list.add(pair1);
            list.add(pair2);
            String result = HttpUtil.doGetJson(elmUrl, list);
            if (StringUtils.isNotBlank(result)) {
                JSONObject json = JSONObject.parseObject(result);
                if ("true".equals(json.getString("success"))) {
                    JSONArray array = json.getJSONArray("result");
                    if (!CollectionUtils.isEmpty(array)) {
                        for (Object jsonObject : array) {
                            SpiVO spiVO = new SpiVO();
                            JSONObject data = (JSONObject) jsonObject;
                            String padNo = data.getString("padNo");
                            String arrayId = data.getString("arrayId");
                            if (StringUtils.isNotBlank(padNo) && StringUtils.isNotBlank(arrayId)) {
                                spiVO.setPadNo(padNo);
                                spiVO.setArrayId(arrayId);
                                padNos.add(spiVO);
                            }
                        }
                    }
                } else {
                    log.info("采集文件：" + filePath + "时，调用ELM接口出错，原因：" + json.getString("message"));
                }
            } else {
                log.info("采集文件：" + filePath + "时，调用ELM接口出错，result为空！");
                return new ArrayList<>();
            }
            if (padNos.size() <= 0) {
                log.info("采集文件：" + filePath + "出错，原因：" + "通过设备类型" + equType + "和机型" + machineType + "没有查询到数据，padNos为空，不采集该文件！");
                return new ArrayList<>();
            }
            int flag = 0;
            while ((line = bufferedReader.readLine()) != null) {
                //第二行列名跳过
                if (index == 1) {
                    index++;
                    continue;
                }
                //设备数据，即txt文件中的数据
                EquDetailsInfo equDetailsInfo = new EquDetailsInfo();
                String[] data = line.split(",");
                //跟ELM接口得到的数据进行比较，得到需要读取的行
                String arrayId = data[1];
                String padNo = data[3];
                for (int i = 0; i < padNos.size(); i++) {
                    SpiVO spiVO = padNos.get(i);
                    try {
                        if (padNo.equals(spiVO.getPadNo()) && arrayId.equals(spiVO.getArrayId()) && data.length >= 14) {
                            flag = 1;
                            equDetailsInfo.setMachineType(machineType);
                            equDetailsInfo.setSn(machineType);
                            equDetailsInfo.setItemNum(machineType);
                            equDetailsInfo.setBoardId(data[0]);
                            equDetailsInfo.setArrayId(data[1]);
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
                    } catch (Exception e) {
                        log.info("采集文件：" + filePath + "出错，原因：" + e.toString());
                    }
                }
            }
            if (flag == 0) {
                log.info("无法采集文件：" + filePath + "，原因：" + "通过设备类型" + equType + "和机型" + machineType + "查询到数据padNo：" + padNos.toString() + "在该文件中不存在！");
            }
        } catch (UnsupportedEncodingException e) {
            log.info("采集文件:{},出错，原因:{}",filePath,e.toString());
        } catch (IOException e) {
            log.info("采集文件:{},出错，原因:{}",filePath,e.toString());
        } catch (Exception e){
            log.info("采集文件:{},出错，原因:{}",filePath,e.toString());
        } finally {
            try {
                if(null != bufferedReader){
                    bufferedReader.close();
                }
                if(null != reader){
                    reader.close();
                }
                if(null != is){
                    is.close();
                }
            } catch (IOException e) {
                log.info("关闭流错误,原因：{}",e.toString());
            }
        }
        return equDetailsInfos;
    }

    public List<ViInfo> readTxtForVi(InputStream is,String equType,File file){
        //调用ELM接口得到对应的信息
        List<NameValuePair> list = new LinkedList<>();
        List<String> info = new ArrayList<>();

        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        //设备详细数据
        List<ViInfo> viInfos = null;
        try {
            reader = new InputStreamReader(is,"GBK");
            bufferedReader = new BufferedReader(reader);
            int index = 1;
            viInfos = new ArrayList<>();
            //读取第一行，取机型,sn
            String line = bufferedReader.readLine();
            if(StringUtils.isBlank(line)){
                log.info("文件：" + file.getAbsolutePath() + "读取不到机型，设备名，直接返回！");
                return new ArrayList<>();
            }
            String[] strs = line.split(",");
            //机型
            String str2 = strs[1];
            String machineType = "";
            //sn
            String str3 = strs[3];
            String sn = "";
            int bIndex = -1;
            if(StringUtils.isNotBlank(str2)){
                if(str2.contains(" ")){
                    bIndex = str2.lastIndexOf(" ");
                }else{
                    bIndex = str2.lastIndexOf(":");
                }
                machineType = str2.substring(bIndex+1,str2.length());
                if(str2.contains("(")){
                    machineType = str2.substring(bIndex+1,str2.lastIndexOf("("));
                }
            }
            //机型
            String itemNum = machineType;
            int aIndex = -1;
            if(StringUtils.isNotBlank(str3)){
                if(str3.contains(" ")){
                    aIndex = str3.lastIndexOf(" ");
                }else{
                    aIndex = str3.lastIndexOf(":");
                }
                sn = str3.substring(aIndex+1,str3.length());
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
                    if(!CollectionUtils.isEmpty(array)){
                        for(Object jsonObject : array){
                            JSONObject data = (JSONObject) jsonObject;
                            String padNo = data.getString("padNo");
                            if(StringUtils.isNotBlank(padNo)){
                                String[] sts = padNo.replace("，", ",").split(",");
                                List<String> datas = Arrays.asList(sts);
                                info.addAll(datas);
                            }
                        }
                    }
                }
                else{
                    log.info("采集文件："+file.getAbsolutePath()+"时，调用ELM接口出错，原因："+json.getString("message"));
                }
            }else{
                log.info("采集文件："+file.getAbsolutePath()+"时，调用ELM接口出错，result为空！");
                return new ArrayList<>();
            }
            if(info.size() <= 0){
                log.info("采集文件："+file.getAbsolutePath()+"出错，原因："+"通过设备类型"+equType+"和机型"+machineType+"没有查询到数据，padNos为空，不采集该文件！");
                return new ArrayList<>();
            }
            int flag = 0;
            while((line = bufferedReader.readLine()) != null){
                //第二行列名跳过
                if(index == 1){
                    index++;
                    continue;
                }
                //设备数据，即txt文件中的数据
                ViInfo viInfo = new ViInfo();
                String[] data = line.split(" ");
                //跟ELM接口得到的数据进行比较，得到需要读取的行
                String ref = data[0];
                for(int i = 0;i < info.size();i++){
                    try {
                        if(ref.equals(info.get(i)) && data.length >=9){
                            flag = 1;
                            viInfo.setSn(sn);
                            viInfo.setItemNum(itemNum);
                            viInfo.setMachineType(machineType);
                            viInfo.setDX(Double.valueOf(data[5]));
                            viInfo.setDY(Double.valueOf(data[6]));
                            viInfo.setDTheta(Double.valueOf(data[7]));
                            viInfo.setErrCode(data[8]);
                            viInfos.add(viInfo);
                            break;
                        }
                    } catch (Exception e) {
                        log.info("采集文件："+file.getAbsolutePath()+"出错，原因："+e.toString());
                    }
                }
            }
            if(flag == 0){
                log.info("无法采集文件："+file.getAbsolutePath()+"，原因："+"通过设备类型"+equType+"和机型"+machineType+"查询到数据padNo："+info.toString()+"在该文件中不存在！");
            }
            bufferedReader.close();
            reader.close();
            is.close();
        }catch (UnsupportedEncodingException e){
            log.info("文件:{},读取错误！原因:{}",file.getAbsolutePath(),e.toString());
        }catch (IOException e){
            log.info("文件:{},读取错误！原因:{}",file.getAbsolutePath(),e.toString());
        } catch (Exception e){
            log.info("文件:{},读取错误！原因:{}",file.getAbsolutePath(),e.toString());
        } finally {
            try {
                if(null != bufferedReader){
                    bufferedReader.close();
                }
                if(null != reader){
                    reader.close();
                }
                if(null != is){
                    is.close();
                }
            } catch (IOException e) {
                log.info("关闭流错误,原因：{}",e.toString());
            }
        }
        return viInfos;
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
            //采集的文件夹的上上层目录
            File pFile = new File(oldPath).getParentFile().getParentFile();
            String pFileName = pFile.getName();

            //创建设备文件夹
            String ePath = newPath + "\\"+pFileName+"\\" + eName;
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
            log.info("复制文件夹:"+eName+",内容操作出错，原因："+e);
        }
    }
    public static void copyFile(File file,String path){
        if(file.isFile()){
            //得到上级文件夹
            File pFile = file.getParentFile();
            String pName = pFile.getName();
            //形成新的文件夹
            String newPath = path +"\\"+ pName;
            File newFile = new File(newPath);
            newFile.mkdirs();
            try {
                FileInputStream input = new FileInputStream(file);
                FileOutputStream output = new FileOutputStream(newFile.getAbsolutePath() + "/" + (file.getName()).toString());
                byte[] buffer = new byte[1024 * 64];
                int length;
                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                log.info("复制文件:"+file.getAbsolutePath()+",内容操作出错，原因："+e);
            }
        }
    }
    public static void moveFolderForSpi(String eName,String oldPath, String newPath) {
        // 先复制文件
        copyFolder(eName,oldPath, newPath);
        // 则删除源文件，以免复制的时候错乱
        deleteDir(new File(oldPath));
    }
    public static void moveFolderForVi(File file,String path){
        // 先复制文件
        copyFile(file,path);
        // 则删除源文件，以免复制的时候错乱
        deleteDir(file);
    }
}
