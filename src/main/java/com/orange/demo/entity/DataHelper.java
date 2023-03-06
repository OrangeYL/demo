package com.orange.demo.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/6 10:44
 * @description:
 */
public class DataHelper {

    public static Map<String,Object> map = new HashMap<>();

    public static Map<String, Object> getMap() {
        return map;
    }

    public static void setMap(Map<String, Object> map) {
        DataHelper.map = map;
    }

    public DataHelper() {
    }
}
