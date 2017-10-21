package com.cloud.shop.order.web;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import com.cloud.shop.core.utils.CSRandomNumber;
import com.cloud.shop.order.OrderSimulationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单服务的控制器
 * Created by ChengYun on 2017/6/10 Vesion 1.0
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * 根据id来获得订单数据
     * @param orderId orderId
     * @return
     */
//    @MicroServiceDesc(serviceName="order.get")    //框架中核心RPCCaller还不支持"/get/{orderId}"这种方式
//    @RequestMapping(value = "/get/{orderId}",method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public ApiResponse getUserInfo(@PathVariable("orderId") String userId) {  //框架中核心RPCCaller还不支持"/get/{orderId}"这种方式
    @MicroServiceDesc(serviceName="order.get")
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public OrderSimulationResponse getOrderInfo(String orderId, HttpServletRequest request) {
        OrderSimulationResponse orderSimulationResponse;
        if (null == orderId) {
            orderId = request.getParameter("orderId");
        }
        if (null == orderId) {
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","缺少必填参数，请重新输入");
        } else {
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","获得OrderId="+orderId+"的数据成功，现在返回结果");
        }
        return orderSimulationResponse;
    }

    /**
     * 随机模拟: 接口请求成功 与 失败
     * @return
     */
    @MicroServiceDesc(serviceName="order.serviceRandomFail")
    @RequestMapping("/serviceRandomFail")
    @ResponseBody
    public OrderSimulationResponse serviceRandomFail(HttpServletRequest request, HttpServletResponse response) {
        OrderSimulationResponse orderSimulationResponse;
        //获得10以内的随机数，如果是双数，则正常返回，如果不是双数，则超时20秒
        int number = CSRandomNumber.getRandomIntNumber(10);
        if (0 == number % 2) {
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE + "", "随机成功失败接口--正常结果");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.SERVICE_ERROR_CODE + "", "随机成功失败接口--错误");
        }
        return orderSimulationResponse;
    }

    /**
     * 接口请求失败类型，便于调试架构
     * @return
     */
    @MicroServiceDesc(serviceName="order.serviceFail")
    @RequestMapping("/serviceFail")
    @ResponseBody
    public OrderSimulationResponse serviceFail(HttpServletRequest request, HttpServletResponse response) {
        OrderSimulationResponse orderSimulationResponse;
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.SERVICE_ERROR_CODE + "", "随机成功失败接口--错误");
        return orderSimulationResponse;
    }

    /**
     * 模拟随机超时接口
     * @return
     */
    @MicroServiceDesc(serviceName="order.serviceRandomTimeout")
    @RequestMapping("/serviceRandomTimeout")
    @ResponseBody
    public OrderSimulationResponse serviceRandomTimeout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrderSimulationResponse orderSimulationResponse;
        //获得10以内的随机数，如果是双数，则正常返回，如果不是双数，则超时20秒
        int number = CSRandomNumber.getRandomIntNumber(10);
        if(0 == number%2){
            Thread.sleep(1000*18);
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.SERVICE_ERROR_CODE+"","模拟随机超时接口--接口超时");
        }else{
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","模拟随机超时接口--正常结果");
        }
        return orderSimulationResponse;
    }

    /**
     * 模拟超时接口，便于调试架构
     * @return
     */
    @MicroServiceDesc(serviceName="order.serviceTimeout")
    @RequestMapping("/serviceTimeout")
    @ResponseBody
    public OrderSimulationResponse serviceTimeout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrderSimulationResponse orderSimulationResponse;
        Thread.sleep(1000 * 18);
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.SERVICE_ERROR_CODE + "", "模拟随机超时接口--接口超时");
        return orderSimulationResponse;
    }

    /**
     * 模拟随机异常接口(一般而言业务系统会吃掉所有异常，返回接口调用失败的结果)
     * @return
     */
    @MicroServiceDesc(serviceName="order.serviceRandomException")
    @RequestMapping("/serviceRandomException")
    @ResponseBody
    public OrderSimulationResponse serviceRandomException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrderSimulationResponse orderSimulationResponse;
        //获得10以内的随机数，如果是双数，则正常返回，如果不是双数，则超时20秒
        int number = CSRandomNumber.getRandomIntNumber(10);
        if(0 == number%2){
            throw new RuntimeException("模拟随机异常接口--内部Exception错误");
        }else{
            orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","模拟随机异常接口--正常结果");
        }
        return orderSimulationResponse ;
    }

    /**
     * 模拟异常接口，便于调试架构
     * @return
     */
    @MicroServiceDesc(serviceName="order.serviceException")
    @RequestMapping("/serviceException")
    @ResponseBody
    public OrderSimulationResponse serviceException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new RuntimeException("模拟随机异常接口--内部Exception错误");
    }

    /**
     * 获得用户所有订单信息
     * @return
     */
    @MicroServiceDesc(serviceName="order.getOrderByUserId")
    @RequestMapping(value = "/getOrderByUserId",method = {RequestMethod.GET})
    @ResponseBody
    public OrderSimulationResponse getOrderByUserId(HttpServletRequest request) {
        OrderSimulationResponse orderSimulationResponse;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","获得用户所有订单信息的数据成功，现在返回结果");
        return orderSimulationResponse;
    }

    /**
     * 删除用户订单
     * @return
     */
    @MicroServiceDesc(serviceName="order.delOrder")
    @RequestMapping(value = "/delOrder",method = {RequestMethod.GET})
    @ResponseBody
    public OrderSimulationResponse delOrder(HttpServletRequest request) {
        OrderSimulationResponse orderSimulationResponse;
        try {
            Thread.sleep(11);
        } catch (InterruptedException e) {
        }
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","删除用户订单成功，现在返回结果");
        return orderSimulationResponse;
    }


}
