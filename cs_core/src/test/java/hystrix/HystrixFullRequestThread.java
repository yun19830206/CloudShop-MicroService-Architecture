package hystrix;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.initbean.InitBeanToSpringContext;
import com.cloud.shop.dictionary.DictRequestUrl;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class HystrixFullRequestThread implements Runnable {

    /** 循环次数 */
    private final int cycleTimes ;

    public HystrixFullRequestThread(int cycleTimes){
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
                System.out.println("========================ThreadName="+Thread.currentThread().getName()+"第"+i+"次Hystrix请求=======================");
                try {
                    restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_REQUEST_SUCCESS,  ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_SUCCESS结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_SUCCESS结果="+e.getMessage());
                }
                Thread.sleep(1000*2);

                try {
                    restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_REQUEST_FAIL,  ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_FAIL结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_FAIL结果="+e.getMessage());
                }
                Thread.sleep(1000*2);

                try {
                    restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_REQUEST_TIMEOUT,  ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_TIMEOUT结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_TIMEOUT结果="+e.getMessage());
                }
                Thread.sleep(1000*2);

                try {
                    restTemplateResult = restTemplate.getForObject(DictRequestUrl.ORDER_HYSTRIX_REQUEST_EXCEPTION,  ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_EXCEPTION结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  ORDER_HYSTRIX_REQUEST_EXCEPTION结果="+e.getMessage());
                }
                Thread.sleep(1000*2);

                try {
                    restTemplateResult = restTemplate.getForObject(DictRequestUrl.PRODUCT_HYSTRIX_REQUEST_SUCCESS,  ApiResponse.class);
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  PRODUCT_HYSTRIX_REQUEST_SUCCESS结果="+restTemplateResult.getData());
                } catch (RestClientException e) {
                    System.out.println("ThreadName="+Thread.currentThread().getName()+";  PRODUCT_HYSTRIX_REQUEST_SUCCESS结果="+e.getMessage());
                }
                Thread.sleep(1000*2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
