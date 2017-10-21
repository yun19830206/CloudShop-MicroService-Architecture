package com.cloud.shop.gateway.web;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.rpccall.impl.HystrixRPCServiceCallerImpl;
import com.cloud.shop.core.rpccall.impl.NormalRestTemplateRPCServiceCallerImpl;
import com.cloud.shop.core.utils.CSRandomNumber;
import com.cloud.shop.dictionary.DictMicroService;
import com.cloud.shop.order.OrderSimulationResponse;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Api接口服务的控制器
 * Created by ChengYun on 2017/6/10 Vesion 1.0
 */
@Controller
@RequestMapping("/gateway")
public class ApiCallController {

    private final static Logger logger = LoggerFactory.getLogger(ApiCallController.class);

    private static String SERVICE_CALL_TYPE_REST = "rest";
    private static String SERVICE_CALL_TYPE_HYSTRIX = "hystrix";
    private static String SERVICE_INTERFACE_TYPE_NORMAL = "normal";
    private static String SERVICE_INTERFACE_TYPE_FAIL = "fail";
    private static String SERVICE_INTERFACE_TYPE_TIMEOUT = "timeout";
    private static String SERVICE_INTERFACE_TYPE_EXCEPTION = "exception";
    private static String SERVICE_INTERFACE_OTHER_PRODUCT = "product";

    private AtomicLong callCount = new AtomicLong(0);

    /** 使用有服务隔离功能的ServiceCall */
    private final HystrixRPCServiceCallerImpl hystrixRPCServiceCaller ;
    /** 使用没有服务隔离功能的普通Rest功能ServiceCall */
    private final NormalRestTemplateRPCServiceCallerImpl normalRestTemplateRPCServiceCaller ;

    @Autowired
    public ApiCallController(HystrixRPCServiceCallerImpl hystrixRPCServiceCaller, NormalRestTemplateRPCServiceCallerImpl normalRestTemplateRPCServiceCaller){
        this.hystrixRPCServiceCaller = hystrixRPCServiceCaller ;
        this.normalRestTemplateRPCServiceCaller = normalRestTemplateRPCServiceCaller ;
    }

    /**
     * 直接访问方式--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/restNormalCall")
    @ResponseBody
    public ApiResponse restNormalCall() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("orderId",System.currentTimeMillis()/1000+"");
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_GET,requestMap,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("直接访问方式--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
    }
    /**
     * 服务隔离方式--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/hystirxNormalCall")
    @ResponseBody
    public ApiResponse hystirxNormalCall() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        ApiResponse apiResponse;
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("orderId",System.currentTimeMillis()/1000+"");
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_GET,requestMap,OrderSimulationResponse.class,null);
        apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("服务隔离方式--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
        return apiResponse;
    }

    /**
     * 直接访问方式--随机成功与失败(状态)接口
     * @return
     */
    @RequestMapping("/restRandomFail")
    @ResponseBody
    public ApiResponse restRandomFail() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_RANDOM_FAIL,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message(orderSimulationResponse.getResultMessage()).data(orderSimulationResponse.getResultObject()).build();
    }
    /**
     * 服务隔离方式--随机成功与失败(状态)接口
     * @return
     */
    @RequestMapping("/hystrixRandomFail")
    @ResponseBody
    public ApiResponse hystrixRandomFail() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_RANDOM_FAIL,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message(orderSimulationResponse.getResultMessage()).data(orderSimulationResponse.getResultObject()).build();
    }

    /**
     * 直接访问方式--模拟随机超时(18S)接口
     * @return
     */
    @RequestMapping("/restRandomTimeout")
    @ResponseBody
    public ApiResponse restRandomTimeout() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_RANDOM_TIMEOUT,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message(orderSimulationResponse.getResultMessage()).data(orderSimulationResponse.getResultObject()).build();
    }
    /**
     * 服务隔离方式--模拟随机超时(18S)接口
     * @return
     */
    @RequestMapping("hystrixRandomTimeout")
    @ResponseBody
    public ApiResponse hystrixRandomTimeout() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_RANDOM_TIMEOUT,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message(orderSimulationResponse.getResultMessage()).data(orderSimulationResponse.getResultObject()).build();
    }

    /**
     * 直接访问方式--模拟随机异常接口
     * @return
     */
    @RequestMapping("/restRandomException")
    @ResponseBody
    public ApiResponse restRandomException() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_RANDOM_EXCEPTION,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message(orderSimulationResponse.getResultMessage()).data(orderSimulationResponse.getResultObject()).build();
    }
    /**
     * 服务隔离方式--模拟随机异常接口
     * @return
     */
    @RequestMapping("hystrixRandomException")
    @ResponseBody
    public ApiResponse hystrixRandomException() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_RANDOM_EXCEPTION,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message(orderSimulationResponse.getResultMessage()).data(orderSimulationResponse.getResultObject()).build();
    }

    /**
     * 调试服务调用接口--直接调用(rest)/服务隔离(hystrix)--正常接口(normal),失败(状态)接口(fail),超时(18S)接口(timeout),异常接口(exception)
     */
    @RequestMapping("/debugServiceCall")
    @ResponseBody
    public ApiResponse getServiceCallMonitorResult(String callType, String interfaceType) {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse = null ;
        //直接调用(rest)--正常接口(normal),失败(状态)接口(fail),超时(18S)接口(timeout),异常接口(exception)
        if(SERVICE_CALL_TYPE_REST.equals(callType)){
            if(SERVICE_INTERFACE_TYPE_NORMAL.equals(interfaceType)){
                orderSimulationResponse = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_GET,
                        null, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_TYPE_FAIL.equals(interfaceType)){
                orderSimulationResponse = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_FAIL,
                        null, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_TYPE_TIMEOUT.equals(interfaceType)){
                orderSimulationResponse = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_TIMEOUT,
                        null, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_TYPE_EXCEPTION.equals(interfaceType)){
                orderSimulationResponse = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_EXCEPTION,
                        null, OrderSimulationResponse.class,null);
            }
        }
        //服务隔离(hystrix)--正常接口(normal),失败(状态)接口(fail),超时(18S)接口(timeout),异常接口(exception)
        if(SERVICE_CALL_TYPE_HYSTRIX.equals(callType)){
            if(SERVICE_INTERFACE_TYPE_NORMAL.equals(interfaceType)){
                Map<String,String> requestMap = Maps.newHashMap();
                requestMap.put("orderId","zqj123tyu");
                orderSimulationResponse = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_GET,
                        requestMap, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_TYPE_FAIL.equals(interfaceType)){
                orderSimulationResponse = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_FAIL,
                        null, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_TYPE_TIMEOUT.equals(interfaceType)){
                orderSimulationResponse = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_TIMEOUT,
                        null, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_TYPE_EXCEPTION.equals(interfaceType)){
                orderSimulationResponse = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_SERVICE_EXCEPTION,
                        null, OrderSimulationResponse.class,null);
            }
            if(SERVICE_INTERFACE_OTHER_PRODUCT.equals(interfaceType)){
                Map<String,String> requestMap = Maps.newHashMap();
                requestMap.put("productId","zqj123tyu");
                orderSimulationResponse = hystrixRPCServiceCaller.call(DictMicroService.ProductService.PRODUCT_SERVICE_GET,
                        requestMap, OrderSimulationResponse.class,null);
            }
        }
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("调试服务调用接口").data(orderSimulationResponse).build();
    }

    /**
     * 商品服务--模拟调用获得商品服务接口(product.get)
     * @return
     */
    @RequestMapping("getProductInfoNormal")
    @ResponseBody
    public ApiResponse getProductInfoNormal(String productId) {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call("product.get",null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("orderSimulationResponse").data(orderSimulationResponse).build();
    }

    /**
     * 商品服务--模拟调用获得商品服务接口(product.get)
     * @return
     */
    @RequestMapping("getProductInfoHystrix")
    @ResponseBody
    public ApiResponse getProductInfoHystrix(String productId) {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call("product.get",null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("orderSimulationResponse").data(orderSimulationResponse).build();
    }

    /**
     * 商品服务--模拟调用耗时5秒成功反馈的接口(product.serviceUsed5Second)
     * @return
     */
    @RequestMapping("serviceUsed5Second")
    @ResponseBody
    public ApiResponse serviceUsed5Second() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call("product.serviceUsed5Second",null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("商品服务--模拟调用耗时5秒成功反馈的接口").data(orderSimulationResponse).build();
    }


    /**
     * 直接访问方式--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/getOrderByUserIdNormal")
    @ResponseBody
    public ApiResponse getOrderByUserIdNormal() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_GETORDERBYUSERID,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("直接访问方式--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
    }
    /**
     * Hystrix访问--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/getOrderByUserIdHystrix")
    @ResponseBody
    public ApiResponse getOrderByUserIdHystrix() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_GETORDERBYUSERID,null,OrderSimulationResponse.class,null);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("Hystrix访问--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
    }

    /**
     * 直接访问--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/delOrderNormal")
    @ResponseBody
    public ApiResponse delOrderNormal() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        ApiResponse apiResponse;
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.OrderService.ORDER_DELORDER,null,OrderSimulationResponse.class,null);
        apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("直接访问--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
        return apiResponse;
    }
    /**
     * 服务隔离方式--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/delOrderHystrix")
    @ResponseBody
    public ApiResponse delOrderHystrix() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.OrderService.ORDER_DELORDER,null,OrderSimulationResponse.class,null);
        ApiResponse apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("服务隔离方式--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
        return apiResponse;
    }

    /**
     * 直接访问--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/getProductByOrderIdNormal")
    @ResponseBody
    public ApiResponse getProductByOrderIdNormal() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        ApiResponse apiResponse;
        OrderSimulationResponse orderSimulationResponse
                = normalRestTemplateRPCServiceCaller.call(DictMicroService.ProductService.PRODUCT_SERVICE_GETPRODUCTBYORDERID,null,OrderSimulationResponse.class,null);
        apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("直接访问--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
        return apiResponse;
    }
    /**
     * 服务隔离方式--获得Order服务的常规服务接口数据
     * @return
     */
    @RequestMapping("/getProductByOrderIdHystrix")
    @ResponseBody
    public ApiResponse getProductByOrderIdHystrix() {
        logger.error("=====Call Count="+ callCount.incrementAndGet());
        OrderSimulationResponse orderSimulationResponse
                = hystrixRPCServiceCaller.call(DictMicroService.ProductService.PRODUCT_SERVICE_GETPRODUCTBYORDERID,null,OrderSimulationResponse.class,null);
        ApiResponse apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("服务隔离方式--获得Order服务的常规服务接口数据").data(orderSimulationResponse).build();
        return apiResponse;
    }

}
