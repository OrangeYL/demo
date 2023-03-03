package com.orange.demo.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: Li ZhiCheng
 * @create: 2023-03-2023/3/3 16:35
 * @description:
 */
@Component
//关键就是实现ApplicationContextAware 这个接口
public class SpringJobBeanFactory implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringJobBeanFactory.applicationContext=applicationContext;

    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        if (applicationContext == null){
            return null;
        }
        return (T)applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T>  name) throws BeansException {
        if (applicationContext == null){
            return null;
        }
        return applicationContext.getBean(name);
    }
}


