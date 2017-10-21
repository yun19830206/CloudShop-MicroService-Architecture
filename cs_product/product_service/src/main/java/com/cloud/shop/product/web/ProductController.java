package com.cloud.shop.product.web;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import com.cloud.shop.core.utils.CSRandomNumber;
import com.cloud.shop.order.OrderSimulationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品服务服务的控制器
 * Created by ChengYun on 2017/6/10 Vesion 1.0
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    private final static Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * 根据商品id来获得数据
     * @param productId productId
     * @return
     */
//    @MicroServiceDesc(serviceName="product.get")  //框架中核心RPCCaller还不支持"/get/{orderId}"这种方式
//    @RequestMapping("/get/{productId}")
//    @ResponseBody
//    public ApiResponse getUserInfo(@PathVariable("productId") String userId) {    //框架中核心RPCCaller还不支持"/get/{orderId}"这种方式
    @MicroServiceDesc(serviceName="product.get")
    @RequestMapping("/get")
    @ResponseBody
    public OrderSimulationResponse getUserInfo(String productId) {
        OrderSimulationResponse orderSimulationResponse;
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","根据商品id来获得数据");
        return orderSimulationResponse;
    }

    /**
     * 随机模拟: 接口请求成功 与 失败
     * @return
     */
    @MicroServiceDesc(serviceName="product.serviceRandom")
    @RequestMapping("/serviceRandom")
    @ResponseBody
    public ApiResponse serviceRandom(HttpServletRequest request, HttpServletResponse response) {

        //获得10以内的随机数，如果是双数，则正常返回，如果不是双数，则超时20秒
        int number = CSRandomNumber.getRandomIntNumber(10);
        if(0 == number%2){
            return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("随机成功失败接口--正常结果").build();
        }else{
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ApiResponse.ApiResponseBuilder().code(ApiResponse.SERVICE_ERROR_CODE).message("随机成功失败接口--错误").build();
        }
    }

    /**
     * 模拟接口请求超时
     * @return
     */
    @MicroServiceDesc(serviceName="product.serviceTimeout")
    @RequestMapping("/serviceTimeout")
    @ResponseBody
    public ApiResponse serviceTimeout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Thread.sleep(1000*180);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.SERVICE_ERROR_CODE).message("内部服务器超时错误").build();
    }

    /**
     * 模拟接口请求异常(一般而言业务系统会吃掉所有异常，返回接口调用失败的结果)
     * @return
     */
    @MicroServiceDesc(serviceName="product.serviceException")
    @RequestMapping("/serviceException")
    @ResponseBody
    public ApiResponse serviceException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new RuntimeException("内部服务器Exception错误");
    }

    /**
     * 商品服务--模拟调用耗时5秒成功反馈的接口(product.serviceUsed5Second)
     * @return
     */
    @MicroServiceDesc(serviceName="product.serviceUsed5Second")
    @RequestMapping("/serviceUsed5Second")
    @ResponseBody
    public ApiResponse serviceUsed5Second(String productId) throws InterruptedException {
        ApiResponse apiResponse;
        apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("商品接口耗时5秒成功反馈").build();
        Thread.sleep(1000*5);
        return apiResponse;
    }

    /**
     * 获得订单中的所有商品
     * @return
     */
    @MicroServiceDesc(serviceName="product.getProductByOrderId")
    @RequestMapping(value = "/getProductByOrderId",method = {RequestMethod.GET})
    @ResponseBody
    public OrderSimulationResponse getProductByOrderId(HttpServletRequest request) {
        OrderSimulationResponse orderSimulationResponse;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","获得订单中的所有商品，现在返回结果");
        return orderSimulationResponse;
    }

}
