package com.cloud.shop.core.test.thread;

import com.cloud.shop.core.initbean.InitBeanToSpringContext;
import com.cloud.shop.core.test.pojo.HttpRequestResultModel;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.BlockingQueue;

/**
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class OneRequestThread implements Runnable {

    /** 循环次数 */
    private final int cycleTimes ;

    /** 请求地址 */
    private final String requestURl;

    /** 每次请求的间隔时间 */
    private final int requestInterval ;

    /** 测试线程带进来的线程执行存储结果 */
    private final BlockingQueue<HttpRequestResultModel> requestResultQueue ;

    public OneRequestThread(int cycleTimes, String requestUrl, int requestInterval, BlockingQueue<HttpRequestResultModel> requestResultQueue){
        this.cycleTimes = cycleTimes;
        this.requestURl = requestUrl ;
        this.requestInterval = requestInterval ;
        this.requestResultQueue = requestResultQueue ;
    }

    @Override
    public void run() {
        int minRequest = 9999;
        int maxRequest = 0 ;
        int requestCount = 0 ;
        int requestTimeCount = 0 ;
        Object restTemplateResult ;
        InitBeanToSpringContext initSpringRestTemplate = new InitBeanToSpringContext();
        RestTemplate restTemplate = initSpringRestTemplate.restTemplate();
        long startTime ;
        int usedTime;
        try {
            for(int i =0 ; i< cycleTimes; i++){
                requestCount ++ ;
                System.out.println();System.out.println();
                System.out.println("========================ThreadName="+Thread.currentThread().getName()+"第"+i+"次请求=======================");
                startTime = System.currentTimeMillis();
                restTemplateResult = restTemplate.getForObject(requestURl,  Object.class);
                usedTime = (int) (System.currentTimeMillis()-startTime);
                requestTimeCount = requestTimeCount + usedTime;
                if(minRequest>usedTime){
                    minRequest = usedTime ;
                }
                if(maxRequest<usedTime){
                    maxRequest = usedTime;
                }
                System.out.println("ThreadName="+Thread.currentThread().getName()+";  耗时["+usedTime+"]ms;  结果="+restTemplateResult);
                Thread.sleep(requestInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("======================总结ThreadName="+Thread.currentThread().getName()+"最小请求时间:"+minRequest+"; 最大请求时间:"+maxRequest+"; 平均耗时:"+requestTimeCount/requestCount+"MS. =======================");

        HttpRequestResultModel httpRequestResultModel = new HttpRequestResultModel(Thread.currentThread().getName(),minRequest,maxRequest,requestTimeCount/requestCount);
        requestResultQueue.offer(httpRequestResultModel);
    }
}
