package com.orange.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orange.demo.utils.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/8 9:22
 * @description:
 */
@RestController
public class TestController {
    @Value("${elmUrl}")
    private String elmUrl;

    @RequestMapping("/test")
    public void test(){
        List<String> padNos = new ArrayList<>();
        List<NameValuePair> list = new LinkedList<>();
        BasicNameValuePair pair1 = new BasicNameValuePair("equType","SPI");
        BasicNameValuePair pair2 = new BasicNameValuePair("machineType", "LF3083");
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
                            padNos.add(padNo);
                        }
                    }
                }
            }
        }
        padNos.forEach(System.out::println);
    }
}
