package com.orange.demo.job;

import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.entity.EquInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/24 8:57
 * @description:
 */
@Slf4j
public class FileJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        String path = map.get("path").toString();
        String storePath = map.get("storePath").toString();
        String fileName = map.get("fileName").toString();
        //得到path路径下的所有文件夹
        List<File> files = getFile(path);
        if(files.size() <= 0){
            return;
        }
        files.forEach(item ->{
            List<EquInfo> equInfos = new ArrayList<>();
            List<File> list = getFile(item.getAbsolutePath());
            if(list.size() <= 0){
                return;
            }
            for(File e : list){
                EquInfo equInfo = new EquInfo();
                //设备名
                String eName = item.getName();
                equInfo.setEName(eName);

                String filePath = "";
                if(fileName.contains(".txt")){
                    filePath = e.getAbsolutePath()+"\\"+fileName;
                }else{
                    filePath = e.getAbsolutePath() + "\\" + fileName + ".txt";
                }
                File file = new File(filePath);
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    equInfo = readTxt(inputStream,equInfo);
                    equInfos.add(equInfo);
                    //移动文件夹（先复制再删除）
                    moveFolder(eName,e.getAbsolutePath(),storePath);
                } catch (FileNotFoundException exception) {
                    log.info("文件不存在！");
                }
            }
        });
    }
    public EquInfo readTxt(InputStream is,EquInfo equInfo){
        String[] padNos = {"534","535"};
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        String line = null;
        List<EquDetailsInfo> equDetailsInfos = null;
        try {
            reader = new InputStreamReader(is, "GBK");
            bufferedReader = new BufferedReader(reader);
            int index = 1;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            equDetailsInfos = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null){
                EquDetailsInfo equDetailsInfo = new EquDetailsInfo();
                String[] data = line.split(",");
                if(index == 1){
                    equInfo.setEType(data[0]);
                    index++;
                    continue;
                }
                if(index == 2){
                    index++;
                    continue;
                }
                String padNo = data[3];
                for(int i = 0;i < padNos.length;i++){
                    if(padNo.equals(padNos[i])){
                        equDetailsInfo.setBoardId(data[0]);
                        equDetailsInfo.setPadNo(data[3]);
                        equDetailsInfo.setInspStTime(data[8]);
                        equDetailsInfo.setInspVol(Double.valueOf(data[9]));
                        equDetailsInfo.setInspArea(Double.valueOf(data[10]));
                        equDetailsInfo.setInspHei(Double.valueOf(data[11]));
                        equDetailsInfo.setInspX(Double.valueOf(data[12]));
                        equDetailsInfo.setInspY(Double.valueOf(data[13]));
                        equDetailsInfos.add(equDetailsInfo);
                    }
                }
            }
            equInfo.setList(equDetailsInfos);
            bufferedReader.close();
            reader.close();
            is.close();
        } catch (Exception e) {
           log.info("文件读取错误！");
        }
        return equInfo;
    }
    public List<File> getFile(String path){
        if(path == null || path.equals("")){
            return new ArrayList<>();
        }
        return getAllFile(new File(path));
    }
    public List<File> getAllFile(File dirFile){
        // 如果文件夹不存在或着不是文件夹，则返回 null
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
    public  boolean deleteDir(File dir) {
        // 如果是文件夹
        if (dir.isDirectory()) {
            // 则读出该文件夹下的的所有文件
            String[] children = dir.list();
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
        // 读到的是一个文件或者是一个空目录，则可以直接删除
        return dir.delete();
    }

    // 复制某个目录及目录下的所有子目录和文件到新文件夹
    public void copyFolder(String eName,String oldPath, String newPath) {
        try {
            //创建包含设备名的文件夹
            String ePath = newPath + "\\" + eName;
            File eFile = new File(ePath);
            eFile.mkdirs();

            //得到旧文件夹的名字
            String name = new File(oldPath).getName();
            String pathNew = eFile.getAbsolutePath() + "\\"+name;
            //创建文件夹
            File pathFile = new File(pathNew);
            pathFile.mkdirs();
            // 读取整个文件夹的内容到file字符串数组，下面设置一个游标i，不停地向下移开始读这个数组
            File filelist = new File(oldPath);
            String[] file = filelist.list();
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
                    byte[] bufferarray = new byte[1024 * 64];
                    int prereadlength;
                    while ((prereadlength = input.read(bufferarray)) != -1) {
                        output.write(bufferarray, 0, prereadlength);
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
    public  void moveFolder(String eName,String oldPath, String newPath) {
        // 先复制文件
        copyFolder(eName,oldPath, newPath);
        // 则删除源文件，以免复制的时候错乱
        deleteDir(new File(oldPath));
    }

}
