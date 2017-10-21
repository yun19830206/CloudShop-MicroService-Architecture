package com.cloud.shop.user.web;

import com.cloud.shop.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户App默认首页的控制器
 * Created by ChengYun on 2017/6/10 Vesion 1.0
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
    public ApiResponse index(HttpServletRequest request, HttpServletResponse response) {
        String path= request.getContextPath();
        String schme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String basePath = schme + "://" + serverName + ":" + serverPort + path + "/" + request.getRequestURI();
        System.out.println("basePath="+basePath);


        String resultString = "Cloud Shop User用户子系统 Web App Start Success, Now Date SnapShort is "+System.currentTimeMillis();
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.SERVICE_ERROR_CODE).message(resultString).build();
    }
}
