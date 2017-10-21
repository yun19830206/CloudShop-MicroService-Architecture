package com.cloud.shop.order.web;

import com.cloud.shop.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户App默认首页的控制器
 * Created by ChengYun on 2017/7/24 Vesion 1.0
 */
@Controller
public class WebIndexController {

    private final static Logger logger = LoggerFactory.getLogger(WebIndexController.class);

    /**
     * 项目index接口
     * @return
     */
    @ResponseBody
    @RequestMapping("/index")
    public ApiResponse index() {
        String resultString = "Cloud Shop Order订单 Web App Start Success, Now Date SnapShort is "+System.currentTimeMillis();
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.SERVICE_ERROR_CODE).message(resultString).build();
    }
}
