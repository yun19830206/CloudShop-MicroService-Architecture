package com.cloud.shop.core;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 简单测试工具类
 * Created by ChengYun on 2017/7/18 Vesion 1.0
 */
public class StaticMainTestClass {

    private int i = 0;

    //    public StaticMainTestClass(){
//        System.out.println("实例化一个对象");
//    }
    @PostConstruct //初始化方法的注解方式  等同与init-method=init
    public void init() {
        System.out.println("调用初始化方法....");
    }

    @PreDestroy //销毁方法的注解方式  等同于destory-method=destory222
    public void destory() {
        System.out.println("调用销毁化方法....");
    }

    public static void main(String[] args) {
        StaticMainTestClass staticMainTestClass = new StaticMainTestClass();
//        for(int i=0;i<100;i++){
//            System.out.println(CSRandomNumber.getRandomIntNumber(10));
//        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = df.parse("2017-09-26 21:28:30");
            Date end = df.parse("2017-09-27 15:11:56");
            long diff = end.getTime() - start.getTime();//这样得到的差值是微秒级别
            System.out.println("启动时间:" + diff / 1000);
//            long days = diff / (1000 * 60 * 60 * 24);
//
//            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
//            System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
        } catch (Exception e) {
        }
    }
}
