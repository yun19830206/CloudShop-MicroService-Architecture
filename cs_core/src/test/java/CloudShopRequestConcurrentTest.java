import hystrix.HystrixFullRequestThread;
import hystrix.HystrixOneServiceCallRequestThread;
import hystrix.HystrixRequestAllSucessThread;
import hystrix.HystrixSuccessRequestThread;
import normalresttemplate.RestTemplateRandomRequestThread;
import normalresttemplate.RestTemplateSuccessRequestThread;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class CloudShopRequestConcurrentTest {


    public static void main(String[] args) throws InterruptedException {
        CloudShopRequestConcurrentTest appMain = new CloudShopRequestConcurrentTest();
        //普通RestTemplate全部请求测试
        //appMain.normalRandomFullConcurrent(400,0.1, 30);
        //appMain.normalSuccessFullConcurrent(400,0.1, 3);

        //Hystrix全部请求测试
        //appMain.hystrixRandomRequest(250,0.1, 10);
        //appMain.hystrixFullSucessRequest(1,0.1, 3);


        //线程池/队列/信号量是否饱满:调用一个耗时5秒的成功接口(Hystrix超时设置10秒,RestTemplate超时设置15秒),并发30个请求
        //结论:能看线程池满了之后的效果，直接调用fallBack()。但是不知道为什么是一共25个请求之后，后面5个再次执行run()方法了，况且发现队列设置没有体现出效果
        appMain.debugHystrixCall(500,100, 2000,"http://127.0.0.1:9002/order/get?orderId=4567",2000);

        while (true){
            Thread.sleep(1000*60*10);
        }
    }

    /** 单个Hystrix请求服务 */
    private void debugHystrixCall(int threadCount, int addThreadInterval, int cycleTimes, String requestUrl, int requestInterval) throws InterruptedException {
        AtomicLong ThreadCount = new AtomicLong(0);
        for(int i=0; i<threadCount; i++ ){
            HystrixOneServiceCallRequestThread requestWithRestTemplate = new HystrixOneServiceCallRequestThread(cycleTimes,requestUrl,requestInterval);
            Thread thread = new Thread(requestWithRestTemplate);
            thread.start();
            Thread.sleep(addThreadInterval);
            ThreadCount.incrementAndGet();
        }
        System.out.println("=====oneThreadCallCount="+ThreadCount.incrementAndGet());
    }


    /**
     * 普通RestTemplate请求测试:随机报错
     * @param threadCount           线程总数
     * @param addThreadInterval     增加测试线程见的停顿时间
     * @param cycleTimes            每个接口请求的讲个时间
     */
    private void normalRandomFullConcurrent(int threadCount, double addThreadInterval, int cycleTimes) throws InterruptedException {
        AtomicLong ThreadCount = new AtomicLong(0);
        for(int i=0; i<threadCount; i++ ){
            RestTemplateRandomRequestThread requestWithRestTemplate = new RestTemplateRandomRequestThread(cycleTimes);
            Thread thread = new Thread(requestWithRestTemplate);
            thread.start();
            Thread.sleep((long) (1000*addThreadInterval));
            ThreadCount.incrementAndGet();
        }
        System.out.println("=====oneThreadCallCount="+ThreadCount.incrementAndGet());
    }

    /**
     * 普通RestTemplate请求测试：全部正常请求，时间都是30ms内
     * @param threadCount           线程总数
     * @param addThreadInterval     增加测试线程见的停顿时间
     * @param cycleTimes            每个接口请求的讲个时间
     */
    private void normalSuccessFullConcurrent(int threadCount, double addThreadInterval, int cycleTimes) throws InterruptedException {
        AtomicLong ThreadCount = new AtomicLong(0);
        for(int i=0; i<threadCount; i++ ){
            RestTemplateSuccessRequestThread requestWithRestTemplate = new RestTemplateSuccessRequestThread(cycleTimes);
            Thread thread = new Thread(requestWithRestTemplate);
            thread.start();
            Thread.sleep((long) (1000*addThreadInterval));
            ThreadCount.incrementAndGet();
        }
        System.out.println("=====oneThreadCallCount="+ThreadCount.incrementAndGet());
    }

    /**
     * Hystrix全部请求测试：先请求100个全部成功的，然后在请求可能失败的
     * @param threadCount           线程总数
     * @param addThreadInterval     增加测试线程见的停顿时间
     * @param cycleTimes            每个接口请求的讲个时间
     */
    private void hystrixRandomRequest(int threadCount, double addThreadInterval, int cycleTimes) throws InterruptedException {
        //Hystrix请求预处理(搞100个请求都成功)
        for(int i=0; i<10; i++ ){
            HystrixRequestAllSucessThread  requestWithHystrixAllSucess = new HystrixRequestAllSucessThread(10);
            Thread thread = new Thread(requestWithHystrixAllSucess);
            thread.start();
            Thread.sleep((long) (1000*addThreadInterval));
        }
        Thread.sleep(1000*3);  //10秒窗口期

        //Hystrix请求预处理(搞100个请求都成功)
        for(int i=0; i<threadCount; i++ ){
            HystrixFullRequestThread requestWithHystrix = new HystrixFullRequestThread(cycleTimes);
            Thread thread = new Thread(requestWithHystrix);
            thread.start();
            Thread.sleep((long) (1000*addThreadInterval));
        }
    }

    /**
     * Hystrix全部请求测试：先请求100个全部成功的，然后在请求可能失败的
     * @param threadCount           线程总数
     * @param addThreadInterval     增加测试线程见的停顿时间
     * @param cycleTimes            每个接口请求的讲个时间
     */
    private void hystrixFullSucessRequest(int threadCount, double addThreadInterval, int cycleTimes) throws InterruptedException {
        //Hystrix请求预处理(搞100个请求都成功)
        for(int i=0; i<threadCount; i++ ){
            HystrixSuccessRequestThread requestWithHystrix = new HystrixSuccessRequestThread(cycleTimes);
            Thread thread = new Thread(requestWithHystrix);
            thread.start();
            Thread.sleep((long) (1000*addThreadInterval));
        }
    }


}
