package com.cloud.shop.user.web;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import com.cloud.shop.core.utils.CSRandomNumber;
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
 * 用户服务的控制器
 * Created by ChengYun on 2017/6/10 Vesion 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 根据id来删除数据
     * @param userId
     * @return
     */
//    @MicroServiceDesc(serviceName="user.get") //框架中核心RPCCaller还不支持"/get/{orderId}"这种方式
//    @RequestMapping(value = "/get/{userId}", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResponse getUserInfo(@PathVariable("userId") String userId) {   //框架中核心RPCCaller还不支持"/get/{orderId}"这种方式
    @MicroServiceDesc(serviceName="user.get")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse getUserInfo(String userId) {

        ApiResponse apiResponse;
        if (null == userId) {
            apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("缺少必填参数，请重新输入").build();
        } else {
            apiResponse = new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("获得UserId="+userId+"的数据成功，现在返回结果").build();
        }
        return apiResponse;
    }

    /**
     * 随机模拟: 接口请求成功 与 失败
     * @return
     */
    @MicroServiceDesc(serviceName="user.serviceRandom")
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
    @MicroServiceDesc(serviceName="user.serviceTimeout")
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
    @MicroServiceDesc(serviceName="user.serviceException")
    @RequestMapping("/serviceException")
    @ResponseBody
    public ApiResponse serviceException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new RuntimeException("内部服务器Exception错误");
    }

    /**
     * debug接口
     * @return
     */
    @RequestMapping("/debug")
    @ResponseBody
    public ApiResponse debug(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("debug接口调用成功").build();
    }

}
