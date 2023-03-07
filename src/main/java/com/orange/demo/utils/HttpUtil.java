package com.orange.demo.utils;

import com.alibaba.fastjson.JSONObject;
import com.sun.javafx.fxml.builder.URLBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/6 17:12
 * @description:
 */
public class HttpUtil {

    @Value(value = "${elmUrl}")
    private  String elmUrl;

    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(response!=null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doGetJson(String url,String par1,String par2){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("equType",par1);
            uriBuilder.setParameter("machineType",par2);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            response = httpClient.execute(httpGet);
            resultString = EntityUtils.toString(response.getEntity(),"utf-8");

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(response!=null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
    public static String doGetJson(String url, List<NameValuePair> list){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameters(list);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            response = httpClient.execute(httpGet);
            resultString = EntityUtils.toString(response.getEntity(),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(response!=null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
}
