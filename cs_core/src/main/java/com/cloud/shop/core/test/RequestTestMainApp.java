package com.cloud.shop.core.test;

import com.cloud.shop.core.test.pojo.HttpRequestResultModel;
import com.cloud.shop.core.test.thread.OneRequestThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 并发测试某个接口使用。
 * Created by ChengYun on 2017/10/15 Version 1.0
 */
public class RequestTestMainApp {



    public static void main(String[] args) throws InterruptedException {

        /**
         * 通用型 单接口 压力测试工具：
         *  1: 可设置并发线程数、线程增加间隔时间、请求循环次数、请求URL地址、每个请求间隔时间
         */
        String requestUrl = "http://192.168.4.181:9002/order/get?orderId=4567" ;
        concurrentOneRequestTest(3000,50, 50,2000, requestUrl);



    }

    /**
     * 通用型 单接口 压力测试工具：可设置并发线程数、线程增加间隔时间、请求循环次数、每个请求间隔时间、请求URL地址
     * @param threadCount           并发线程数
     * @param addThreadIntervalMS   线程增加间隔时间
     * @param cycleTimes            请求循环次数
     * @param requestIntervalMS     每个请求间隔时间ThindTime
     * @param requestUrl            请求URL地址
     * @throws InterruptedException
     */
    public static void concurrentOneRequestTest(int threadCount, int addThreadIntervalMS, int cycleTimes, int requestIntervalMS, String requestUrl) throws InterruptedException {
        //控制与判断线程什么时候结束
        BlockingQueue<Thread> threadQueue=new ArrayBlockingQueue<>(threadCount);
        //记录每个线程的执行结果：最小请求时间; 最大请求时间; 平均耗时
        BlockingQueue<HttpRequestResultModel> requestResultQueue=new ArrayBlockingQueue<>(threadCount);

        //创建线程并启动任务
        for(int i=0; i<threadCount; i++ ){
            OneRequestThread requestWithRestTemplate = new OneRequestThread(cycleTimes,requestUrl,requestIntervalMS,requestResultQueue);
            Thread thread = new Thread(requestWithRestTemplate);
            threadQueue.add(thread);
            thread.start();
            Thread.sleep(addThreadIntervalMS);

        }
        System.out.println("=====concurrentOneRequestTest start success");

        //等待任务结束
        Thread oneThread = threadQueue.peek();
        while (null != oneThread){
            //Thread.State.TERMINATED说明线程已经运行完成，需要移出，判断下一个. 不是TERMINATED状态说明还没有结束，需要再等等。
            if(Thread.State.TERMINATED.equals(oneThread.getState())){
                oneThread = threadQueue.poll();
            }else{
                Thread.sleep(1000);
                oneThread = threadQueue.peek();
            }
        }

        //任务结束，统计结果
        HttpRequestResultModel[] requestResultArray = new HttpRequestResultModel[requestResultQueue.size()];
        requestResultArray = requestResultQueue.toArray(requestResultArray);
        int minRequest = requestResultArray[0].getMinRequestUsedTime();
        int maxRequest = requestResultArray[0].getMaxRequestUsedTime();
        int requestCount = requestResultArray[0].getRequestAverageUsedTime();
        for(int i=1 ; i<requestResultArray.length; i++){
            if(minRequest > requestResultArray[i].getMinRequestUsedTime()){
                minRequest = requestResultArray[i].getMinRequestUsedTime() ;
            }
            if(maxRequest < requestResultArray[i].getMaxRequestUsedTime()){
                maxRequest = requestResultArray[i].getMaxRequestUsedTime();
            }
            requestCount = requestCount + requestResultArray[i].getRequestAverageUsedTime() ;
        }
        System.out.println("======================单接口压力测试总结["+requestResultArray.length+":]最小请求时间"+minRequest+"; 最大请求时间:"+maxRequest+"; 平均耗时:"+requestCount/requestResultArray.length+"MS. =======================");
    }
}
