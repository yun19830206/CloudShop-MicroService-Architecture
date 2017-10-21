package normalresttemplate;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.initbean.InitBeanToSpringContext;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.cloud.shop.dictionary.DictRequestUrl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class RestTemplateRandomRequestThread implements Runnable {

    /** 循环次数 */
    private final int cycleTimes ;
    private AtomicLong oneThreadCallCount = new AtomicLong(0);

    public RestTemplateRandomRequestThread(int cycleTimes){
        this.cycleTimes = cycleTimes;
    }

    @Override
    public void run() {
        ApiResponse restTemplateResult ;
        InitBeanToSpringContext initSpringRestTemplate = new InitBeanToSpringContext();
        RestTemplate restTemplate = initSpringRestTemplate.restTemplate();
        try {
            for(int i =0 ; i< cycleTimes; i++){
                System.out.println();System.out.println();
                System.out.println("========================ThreadName="+Thread.currentThread().getName()+"第"+i+"次Normal请求=======================");
                try {
                    restTemplateResult = restTemplate.postForObject(DictRequestUrl.ORDER_NORMAL_REQUEST_SUCCESS, null, ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_SUCCESS结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_SUCCESS结果="+e.getMessage());
                }
                Thread.sleep(1000*2);
                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                try {
                    restTemplateResult = restTemplate.postForObject(DictRequestUrl.ORDER_NORMAL_REQUEST_FAIL, null, ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_FAIL结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_FAIL结果="+e.getMessage());
                }
                Thread.sleep(1000*2);
                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                try {
                    restTemplateResult = restTemplate.postForObject(DictRequestUrl.ORDER_NORMAL_REQUEST_TIMEOUT, null, ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_TIMEOUT结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_TIMEOUT结果="+e.getMessage());
                }
                Thread.sleep(1000*2);
                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());

                try {
                    restTemplateResult = restTemplate.postForObject(DictRequestUrl.ORDER_NORMAL_REQUEST_EXCEPTION, null, ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_EXCEPTION结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_NORMAL_REQUEST_EXCEPTION结果="+e.getMessage());
                }
                Thread.sleep(1000*2);
                System.out.println("=====ThreadName="+Thread.currentThread().getName()+" oneThreadCallCount="+oneThreadCallCount.incrementAndGet());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
