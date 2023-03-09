package com.orange.demo.utils;


import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

    /**
     * 获取Properties对象
     * @return
     */
    public static Properties getProperties(){
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            //data.properties在resources目录下
            inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            System.out.println("data.properties文件未找到!");
        } catch (IOException e) {
            System.out.println("出现IOException");
        } finally {
            try {
                if (null != inputStream){
                    inputStream.close();
                }
            } catch (IOException e) {
                System.out.println("data.properties文件流关闭出现异常");
            }
        }
        return properties;
    }

    /**
     * 根据key查询value值
     * @param key key
     * @return
     */
    public static String getValue(String key){
        Properties properties = getProperties();
        String value = properties.getProperty(key);
        return value;
    }

    /**
     * 新增/修改数据
     * @param key
     * @param value
     */
    public static void setValue(String key, String value){
        Properties properties = getProperties();
        properties.setProperty(key, value);
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        path = (path + "data.properties").substring(1, (path + "data.properties").length());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            properties.store(fileOutputStream, "注释");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileOutputStream){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                System.out.println("data.properties文件流关闭出现异常");
            }
        }
    }

}
