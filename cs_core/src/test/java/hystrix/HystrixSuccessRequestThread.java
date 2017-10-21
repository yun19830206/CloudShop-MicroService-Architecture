package hystrix;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.initbean.InitBeanToSpringContext;
import com.cloud.shop.dictionary.DictRequestUrl;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class HystrixSuccessRequestThread implements Runnable {

    /** 循环次数 */
    private final int cycleTimes ;
    private AtomicLong oneThreadCallCount = new AtomicLong(0);

    public HystrixSuccessRequestThread(int cycleTimes){
        this.cycleTimes = cycleTimes;
    }

    @Override
    public void run() {
//        try {
//            Thread.sleep(1000*2);
//        } catch (InterruptedException e) {
//        }
        ApiResponse restTemplateResult ;
        InitBeanToSpringContext initSpringRestTemplate = new InitBeanToSpringContext();
        RestTemplate restTemplate = initSpringRestTemplate.restTemplate();
        long startTime ;
        try {
            for(int i =0 ; i< cycleTimes; i++){
                System.out.println();System.out.println();
                System.out.println("========================ThreadName="+Thread.currentThread().getName()+"第"+i+"次Normal请求=======================");
                startTime = System.currentTimeMillis();
                restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_REQUEST_SUCCESS, ApiResponse.class);
                System.out.println("ThreadName="+Thread.currentThread().getName()+";  耗时["+(System.currentTimeMillis()-startTime)+"]ms;  ORDER_HYSTRIX_REQUEST_SUCCESS结果="+restTemplateResult.getData());
                Thread.sleep(1000*2);
//                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                startTime = System.currentTimeMillis();
                restTemplateResult = restTemplate.getForObject(DictRequestUrl.PRODUCT_HYSTRIX_REQUEST_SUCCESS,  ApiResponse.class);
                System.out.println("ThreadName="+Thread.currentThread().getName()+";  耗时["+(System.currentTimeMillis()-startTime)+"]ms;  PRODUCT_HYSTRIX_REQUEST_SUCCESS结果="+restTemplateResult.getData());
                Thread.sleep(1000*2);
//                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                startTime = System.currentTimeMillis();
                restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_SUCCESS_GET_ORDER_BYUSERID,  ApiResponse.class);
                System.out.println("ThreadName="+Thread.currentThread().getName()+";  耗时["+(System.currentTimeMillis()-startTime)+"]ms;  ORDER_HYSTRIX_SUCCESS_GET_ORDER_BYUSERID结果="+restTemplateResult.getData());
                Thread.sleep(1000*2);
//                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                startTime = System.currentTimeMillis();
                restTemplateResult = restTemplate.getForObject(DictRequestUrl.PRODUCT_HYSTRIX_REQUEST_GET_PRDUCT_BYORDERID,  ApiResponse.class);
                System.out.println("ThreadName="+Thread.currentThread().getName()+";  耗时["+(System.currentTimeMillis()-startTime)+"]ms;  PRODUCT_HYSTRIX_REQUEST_GET_PRDUCT_BYORDERID结果="+restTemplateResult.getData());
                Thread.sleep(1000*2);
//                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                startTime = System.currentTimeMillis();
                restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_SUCCESS_DEL_ORDER,  ApiResponse.class);
                System.out.println("ThreadName="+Thread.currentThread().getName()+";  耗时["+(System.currentTimeMillis()-startTime)+"]ms;  ORDER_HYSTRIX_SUCCESS_DEL_ORDER结果="+restTemplateResult.getData());
                Thread.sleep(1000*2);
//                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
