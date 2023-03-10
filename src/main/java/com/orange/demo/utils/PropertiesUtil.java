package com.orange.demo.utils;


import java.io.*;
import java.util.Properties;

public class PropertiesUtil {
    /**
     * 获取Properties对象
     * @return
     */
    public static Properties doGetPropertiesJar(){
        System.out.println("（1）jar包路径");
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            String path = PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File file = new File(path);
            String parent = file.getParent();
            System.out.println("（1）===> "+ parent);
            File file1 = new File(parent, "data.properties");
            inputStream = new FileInputStream(file1);
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
    public static Properties doGetPropertiesNoJar(){
        System.out.println("（2）不是jar包路径");
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

    public static Properties getProperties(){
        if(isJar()){
            return doGetPropertiesJar();
        }else{
            return doGetPropertiesNoJar();
        }
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
        String newPath = "";
        if(isJar()){
            System.out.println("(3)jar包路径");
            String path = PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File file = new File(path);
            newPath = file.getParent();
            newPath =(newPath +"\\"+ "data.properties");
            System.out.println("3===> " + newPath);
        }else{
            System.out.println("（4）不是jar包路径");
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            newPath = (path + "data.properties").substring(1, (path + "data.properties").length());
            System.out.println("4===> " + newPath);
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(newPath);
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
    /**
     * 如果不是 jar包，会获取到 /target/classes 目录
     * 因此可以通过此判断是否是 jar 还是开发
     * @return
     */
    public static String getJarPath() {
        return PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    /**
     * 判断是不是从 Jar 包读取的情况
     * 如果是，则该路径下的不是文件夹
     * @return
     */
    public static boolean isJar() {
        return !(new File(getJarPath()).isDirectory());
    }

}
